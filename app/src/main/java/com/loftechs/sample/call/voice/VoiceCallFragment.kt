package com.loftechs.sample.call.voice

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.appbar.MaterialToolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.common.event.CallStateChangeEvent
import com.loftechs.sample.common.event.KeyActionEvent
import com.loftechs.sample.component.CallAnimationButton
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.model.api.CallManager
import com.loftechs.sample.utils.DateFormatUtil
import com.loftechs.sdk.call.LTCallStatusCode
import com.loftechs.sdk.call.core.LTCallState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class VoiceCallFragment : AbstractFragment(), VoiceCallContract.View, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, CallAnimationButton.AnswerDeclineListener {

    private var mPresenter: VoiceCallContract.Presenter<VoiceCallContract.View>? = null

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mAcceptButton: CallAnimationButton
    private lateinit var mDeclineButton: ImageButton
    private lateinit var mCircleProfile: ImageView
    private lateinit var mInCallProfile: ImageView
    private lateinit var mSpeaker: ToggleButton
    private lateinit var mMute: ToggleButton
    private lateinit var mPause: ToggleButton
    private lateinit var mNickname: TextView
    private lateinit var mStatus: TextView
    private lateinit var mControlAudioLayout: LinearLayout
    private lateinit var mEmptyLayout: View

    companion object {
        fun newInstance(): VoiceCallFragment {
            return VoiceCallFragment()
        }

        private val TAG = VoiceCallFragment::class.java.simpleName
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_call_voice, container, false)
        initView(root)
        return root
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: VoiceCallPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
        mPresenter?.selectCallByStatusType()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(root: View) {
        mToolbar = root.findViewById(R.id.top_bar)
        mAcceptButton = root.findViewById(R.id.answer_button)
        mDeclineButton = root.findViewById(R.id.decline_btn)
        mCircleProfile = root.findViewById(R.id.circle_profile)
        mNickname = root.findViewById(R.id.caller_name)
        mStatus = root.findViewById(R.id.status_txt)
        mInCallProfile = root.findViewById(R.id.in_call_profile)
        mControlAudioLayout = root.findViewById(R.id.control_audio)
        mSpeaker = root.findViewById(R.id.speaker)
        mMute = root.findViewById(R.id.mute)
        mPause = root.findViewById(R.id.pause)
        mEmptyLayout = root.findViewById(R.id.empty_view)
        mDeclineButton.setOnClickListener(this)
        mSpeaker.setOnCheckedChangeListener(this)
        mMute.setOnCheckedChangeListener(this)
        mPause.setOnCheckedChangeListener(this)
        mToolbar.setNavigationOnClickListener {
            finishView()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.decline_btn -> {
                mPresenter?.hangup()
                mAcceptButton.stopRingingAnimation()
            }
        }
    }

    override fun enableIncomingCallView() {
        mInCallProfile.visibility = View.GONE
        mControlAudioLayout.visibility = View.GONE
        mAcceptButton.startRingingAnimation()
        mAcceptButton.setAnswerDeclineListener(this)
    }

    override fun enableInCallView() {
        mCircleProfile.visibility = View.GONE
        mAcceptButton.visibility = View.GONE
        mEmptyLayout.visibility = View.GONE
        mInCallProfile.visibility = View.VISIBLE
        mControlAudioLayout.visibility = View.VISIBLE
    }

    override fun setNickname(nickname: String) {
        mNickname.text = nickname
    }

    override fun refreshCallStatus() {
        Timber.tag(TAG).d(" CallManager.callState : ${CallManager.callState}")
        mSpeaker.isChecked = CallManager.isSpeakerOn
        mMute.isChecked = CallManager.isCallMuted
        mPause.isChecked = CallManager.isCallHeld
        if (CallManager.callState == LTCallState.CONNECTED) {
            enableInCallView()
        }
    }

    override fun onCheckedChanged(p0: CompoundButton, p1: Boolean) {
        when (p0.id) {
            R.id.speaker -> {
                if (p1) {
                    CallManager.routeAudioToSpeaker()
                } else {
                    CallManager.routeAudioToReceiver()
                }
            }
            R.id.mute -> {
                CallManager.isCallMuted = p1
            }
            R.id.pause -> {
                CallManager.isCallHeld = p1
            }
        }
    }

    private fun parseCallEventByStatus(status: LTCallStatusCode) {
        mStatus.text = status.name
        when (status) {
            LTCallStatusCode.HANGUP,
            LTCallStatusCode.CALLEE_DECLINE,
            LTCallStatusCode.MISS,
            -> {
                mPresenter?.hangup()
            }
            else -> {
            }
        }
    }

    override fun finishView() {
        activity?.onBackPressed()
    }

    override fun onAnswered() {
        Timber.tag(TAG).d("onAnswered")
        enableInCallView()
        mPresenter?.acceptCall()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CallStateChangeEvent) {
        if (event.state != null) {
            parseCallEventByStatus(event.state)
        } else {
            mStatus.text = DateFormatUtil.formatTime(event.duration)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: KeyActionEvent) {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            CallManager.adjustVolume(1)
        } else if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            CallManager.adjustVolume(-1)
        }
    }

    override fun setAvatar(uri: Uri?) {
        mCircleProfile.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        mInCallProfile.loadImageWithGlide(R.drawable.ic_profile, uri, false)
    }
}