package com.loftechs.sample.base

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.loftechs.sample.R
import com.loftechs.sample.component.ProgressDialog

abstract class AbstractFragment : Fragment(), BaseContract.BaseView {

    abstract fun getPresenter(): BaseContract.BasePresenter
    abstract fun clearPresenter()

    private val mProgressDialog: ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            getPresenter().initBundle(it)
        }
        getPresenter().create()
    }

    override fun onResume() {
        super.onResume()
        getPresenter().resume()
    }

    override fun onPause() {
        super.onPause()
        getPresenter().pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPresenter().destroy()
        clearPresenter()
    }

    override fun showProgressDialog() {
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    override fun dismissProgressDialog() {
        mProgressDialog.dismiss()
    }

    override fun showSnackBar(messageResourceID: Int) {
        view?.let {
            Snackbar.make(it, messageResourceID, LENGTH_LONG).show()
        }
    }

    override fun showErrorDialog(messageResourceID: Int) {
        AlertDialog.Builder(requireContext())
                .setMessage(messageResourceID)
                .setPositiveButton(R.string.common_confirm, null)
                .show()
    }

    fun changeFragment(f: Fragment, intentBundle: Bundle?) {
        changeFragment(f, intentBundle, arrayOf(
                R.anim.enter_animation,
                R.anim.exit_animation,
                R.anim.pop_enter_animation,
                R.anim.pop_exit_animation
        ))
    }

    fun changeFragment(f: Fragment, intentBundle: Bundle?, animationArray: Array<Int>) {
        f.arguments = intentBundle

        requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        animationArray[0],
                        animationArray[1],
                        animationArray[2],
                        animationArray[3]
                )
                .replace(R.id.container, f)
                .addToBackStack(this::class.java.simpleName)
                .commit()
    }

    fun changeFragmentWithoutBackStack(f: Fragment, intentBundle: Bundle?) {
        f.arguments = intentBundle

        requireActivity().supportFragmentManager.run {
            beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_animation,
                            R.anim.exit_animation,
                            R.anim.pop_enter_animation,
                            R.anim.pop_exit_animation
                    )
                    .replace(R.id.container, f)
                    .commit()
        }
    }

}