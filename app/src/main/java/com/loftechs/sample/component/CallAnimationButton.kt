package com.loftechs.sample.component

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.loftechs.sample.R
import com.loftechs.sample.utils.ViewUtil
import kotlin.math.min

class CallAnimationButton : LinearLayout, View.OnTouchListener {
    companion object {
        private const val TOTAL_TIME = 1000
        private const val SHAKE_TIME = 200
        private const val UP_TIME = (TOTAL_TIME - SHAKE_TIME) / 2
        private const val DOWN_TIME = (TOTAL_TIME - SHAKE_TIME) / 2
        private const val SHIMMER_TOTAL = UP_TIME + SHAKE_TIME
        private const val ANSWER_THRESHOLD = 60
    }

    private lateinit var swipeUpText: TextView
    private lateinit var fab: AppCompatImageView
    private lateinit var arrowOne: AppCompatImageView
    private lateinit var arrowTwo: AppCompatImageView
    private lateinit var arrowThree: AppCompatImageView
    private var lastY = 0f
    private var animating = false
    private var complete = false
    private lateinit var animatorSet: AnimatorSet
    private lateinit var listener: AnswerDeclineListener

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialize() {
        orientation = VERTICAL
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        inflate(context, R.layout.call_animation_button, this)
        swipeUpText = findViewById(R.id.swipe_up_text)
        fab = findViewById(R.id.answer)
        arrowOne = findViewById(R.id.arrow_one)
        arrowTwo = findViewById(R.id.arrow_two)
        arrowThree = findViewById(R.id.arrow_three)
        fab.setOnTouchListener(this)
    }

    fun startRingingAnimation() {
        if (!animating) {
            animating = true
            animateElements(0)
        }
    }

    fun stopRingingAnimation() {
        if (animating) {
            animating = false
            resetElements()
        }
    }

    fun setAnswerDeclineListener(listener: AnswerDeclineListener) {
        this.listener = listener
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                resetElements()
                swipeUpText.animate().alpha(0f).setDuration(200).start()
                lastY = event.rawY
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                swipeUpText.clearAnimation()
                swipeUpText.alpha = 1f
                fab.rotation = 0f
                if (Build.VERSION.SDK_INT >= 21) {
                    fab.background.setTint(resources.getColor(R.color.btn_green_normal))
                    fab.drawable.setTint(Color.WHITE)
                }
                animating = true
                animateElements(0)
            }
            MotionEvent.ACTION_MOVE -> {
                val difference = event.rawY - lastY
                val differenceThreshold: Float
                val percentageToThreshold: Float
                var backgroundColor: Int = Color.WHITE
                var foregroundColor: Int = resources.getColor(R.color.btn_green_normal)
                if (difference <= 0) {
                    differenceThreshold = ViewUtil.dpToPx(context, ANSWER_THRESHOLD).toFloat()
                    percentageToThreshold = min(1f, difference * -1 / differenceThreshold)
                    backgroundColor = ArgbEvaluator().evaluate(percentageToThreshold, resources.getColor(R.color.btn_green_normal), resources.getColor(R.color.white)) as Int
                    foregroundColor = if (percentageToThreshold > 0.5) {
                        resources.getColor(R.color.btn_green_normal)
                    } else {
                        Color.WHITE
                    }
                    fab.translationY = difference
                    if (percentageToThreshold == 1f && listener != null) {
                        fab.visibility = INVISIBLE
                        lastY = event.rawY
                        if (!complete) {
                            complete = true
                            listener.onAnswered()
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fab.background.setTint(backgroundColor)
                    fab.drawable.setTint(foregroundColor)
                }
            }
        }
        return true
    }

    private fun animateElements(delay: Int) {
        val fabUp = getUpAnimation(fab)
        val fabDown = getDownAnimation(fab)
        val fabShake = getShakeAnimation(fab)
        animatorSet = AnimatorSet()
        animatorSet.play(fabUp).with(getUpAnimation(swipeUpText))
        animatorSet.play(fabShake).after(fabUp)
        animatorSet.play(fabDown).with(getDownAnimation(swipeUpText)).after(fabShake)
        animatorSet.play(getShimmer(arrowThree, arrowTwo, arrowOne))
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (animating) animateElements(1000)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.startDelay = delay.toLong()
        animatorSet.start()
    }

    private fun getShimmer(vararg targets: View): Animator {
        val animatorSet = AnimatorSet()
        val evenDuration = SHIMMER_TOTAL / targets.size
        val interval = 75
        for (i in targets.indices) {
            animatorSet.play(getShimmer(targets[i], evenDuration + (evenDuration - interval)))
                    .after((interval * i).toLong())
        }
        return animatorSet
    }

    private fun getShimmer(target: View, duration: Int): ObjectAnimator {
        val shimmer = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f, 0f)
        shimmer.duration = duration.toLong()
        return shimmer
    }

    private fun getShakeAnimation(target: View?): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        animator.duration = SHAKE_TIME.toLong()
        return animator
    }

    private fun getUpAnimation(target: View?): ObjectAnimator {
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(target, "translationY", 0.0f, -32f, 0.0f)
//        val animator: ObjectAnimator = ObjectAnimator.ofFloat(target, "translationY", 0, (-1 * ViewUtil.dpToPx(context, 32)).toFloat())
        animator.interpolator = AccelerateInterpolator()
        animator.duration = UP_TIME.toLong()
        return animator
    }

    private fun getDownAnimation(target: View?): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "translationY", 0f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = DOWN_TIME.toLong()
        return animator
    }

    private fun resetElements() {
        animating = false
        complete = false
        if (animatorSet != null) animatorSet.cancel()
        swipeUpText.translationY = 0f
        fab.translationY = 0f
    }

    interface AnswerDeclineListener {
        fun onAnswered()
    }

}