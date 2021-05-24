package com.loftechs.sample.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.R
import com.loftechs.sample.authentication.AuthenticationActivity
import com.loftechs.sample.base.BaseActivity
import com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID
import com.loftechs.sample.extensions.REQUEST_INTRO
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sample.model.PreferenceSetting
import com.loftechs.sample.profile.SetProfileFragment
import com.loftechs.sdk.listener.LTErrorInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val mainFragment: MainFragment by lazy {
        MainFragment.newInstance()
    }

    private val profileFragment: SetProfileFragment by lazy {
        SetProfileFragment.newInstance()
    }

    private val disposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (!AccountHelper.hasExistAccount()) {
                Timber.tag(TAG).d("onCreate ++ !hasExistAccount")
                gotoAuthenticationActivity()
                return
            }

            val subscribe = LTSDKManager.sdkObservable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (!AccountHelper.hasSelfNickname()) {
                            showSetProfileFragment()
                        } else {
                            getDefaultBundle()?.let {
                                showMainFragment(it)
                            } ?: run {
                                Timber.tag(TAG).e("onCreate : bundle is null")
                                resetSDK()
                            }
                        }
                    }, { e ->
                        Timber.tag(TAG).e("initSDK ++ error: $e")
                        if (e is LTErrorInfo && e.errorCode == LTErrorInfo.ErrorCode.INIT_ERROR && e.returnCode == 6000) {
                            AlertDialog.Builder(this)
                                    .setMessage(e.errorMessage)
                                    .setPositiveButton(R.string.common_confirm) { _, _ ->
                                        resetSDK()
                                    }
                                    .setCancelable(false)
                                    .show()
                        }
                    })
            disposable.add(subscribe)
        }
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }

    private fun resetSDK() {
        val subscribe = LTSDKManager.resetSDK()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    PreferenceSetting.clearAllPref()
                    AccountHelper.clearCache()
                    gotoAuthenticationActivity()
                }, {
                    Timber.tag(TAG).e("resetSDK ++ error: $it")
                })
        disposable.add(subscribe)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INTRO) {
            if (!AccountHelper.hasSelfNickname()) {
                showSetProfileFragment()
            } else {
                data?.extras?.let {
                    showMainFragment(it)
                } ?: run {
                    Timber.tag(TAG).e("onActivityResult: REQUEST_INTRO: bundle is null")
                    resetSDK()
                }
            }
        }
    }

    private fun gotoAuthenticationActivity() {
        val intent = Intent(baseContext, AuthenticationActivity::class.java)
        startActivityForResult(intent, REQUEST_INTRO, ActivityOptionsCompat
                .makeCustomAnimation(baseContext, R.anim.slide_in_right, R.anim.slide_out_left)
                .toBundle())
    }

    private fun showMainFragment(bundle: Bundle) {
        initFragment(mainFragment, bundle)
    }

    private fun showSetProfileFragment() {
        initFragment(profileFragment, getDefaultBundle())
    }

    private fun getDefaultBundle(): Bundle? {
        AccountHelper.firstAccount?.let { accountEntity ->
            return Bundle().let {
                it.putString(EXTRA_RECEIVER_ID, accountEntity.userID)
                it
            }
        } ?: return null
    }
}