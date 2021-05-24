package com.loftechs.sample.main

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.common.IntentKey
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber

class MainPresenter : MainContract.Presenter<MainContract.View> {

    private var mView: MainContract.View? = null

    private lateinit var mReceiverID: String
    private val mFragmentTypeList: ArrayList<MainItemType> by lazy {
        arrayListOf(
                MainItemType.CHAT,
                MainItemType.CALL
        )
    }

    override val tabItemCount: Int
        get() = 2

    companion object {
        private val TAG = MainPresenter::class.java.simpleName
    }

    override fun create() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
    }

    override fun bindView(view: MainContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun logout() {
        LTSDKManager.getIMManager(mReceiverID)
                .flatMap { imManager: LTIMManager ->
                    Observable.create { emitter: ObservableEmitter<Boolean> ->
                        imManager.disconnect(object : LTCallbackResultListener<Boolean> {
                            override fun onResult(result: Boolean) {
                                emitter.onNext(true)
                                emitter.onComplete()
                            }

                            override fun onError(errorInfo: LTErrorInfo) {
                                emitter.onNext(false)
                                emitter.onComplete()
                            }
                        })
                    }
                }
                .flatMap {
                    LTSDKManager.resetSDK()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(result: Boolean) {
                        Timber.tag(TAG).d("logoff status : $result")
                    }

                    override fun onError(e: Throwable) {
                        Timber.tag(TAG).e("logoff error e : $e")
                    }

                    override fun onComplete() {
                        (SampleApp.context.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                                .clearApplicationUserData()
                    }
                })
    }

    override fun onFabClick(currentItem: Int) {
        val mainItemType = mFragmentTypeList[currentItem]
        mainItemType.onFabClick(mView)
    }

    override fun getTabStringResourceID(position: Int): Int {
        return when (position) {
            0 -> {
                R.string.main_tab_item_chat
            }
            1 -> {
                R.string.main_tab_item_call
            }
            else -> {
                0
            }
        }
    }

    override fun getFabIconResource(position: Int): Int {
        return when (position) {
            0 -> {
                R.drawable.ic_action_compose
            }
            1 -> {
                R.drawable.ic_action_new_call
            }
            else -> {
                0
            }
        }
    }

    override fun getTabItemType(position: Int): MainItemType {
        return mFragmentTypeList[position]
    }
}