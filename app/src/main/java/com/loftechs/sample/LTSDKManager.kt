package com.loftechs.sample

import com.loftechs.sample.fcm.FCMTokenHelper
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sample.receiver.IMReceiver
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.LTSDKOptions
import com.loftechs.sdk.call.LTCallCenterManager
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.storage.LTStorageManager
import com.loftechs.sdk.utils.LTLog
import io.reactivex.Observable
import timber.log.Timber

object LTSDKManager {
    private val TAG = LTSDKManager::class.java.simpleName
    private var mSDK: LTSDK? = null
    private var userDataReady = false

    private val mIMReceiver: IMReceiver by lazy {
        IMReceiver()
    }

    val sdkObservable: Observable<LTSDK>
        get() {
            if (mSDK == null || !userDataReady) {
                AccountHelper.firstAccount?.let {
                    val options = LTSDKOptions.builder()
                            .context(SampleApp.context)
                            .url(BuildConfig.Auth_API)
                            .licenseKey(BuildConfig.License_Key)
                            .userID(it.userID)
                            .uuid(it.uuid)
                            .build()
                    return LTSDK.init(options)
                            .map { aBoolean: Boolean ->
                                LTLog.i(TAG, "getLTSDK init: $aBoolean")
                                if (aBoolean && it.uuid.isNotEmpty()) {
                                    userDataReady = true
                                }
                                mSDK = LTSDK.getInstance()
                                mSDK
                            }
                } ?: return Observable.error(Throwable("$TAG error: no user in sample app"))
            }
            return Observable.just(mSDK)
        }

    fun getIMManager(receiverID: String): Observable<LTIMManager> {
        return sdkObservable
                .flatMap {
                    val imManager = it.getIMManager<LTIMManager>(receiverID)
                    if (imManager.isConnected) {
                        Observable.just(imManager)
                    } else {
                        initIMConnection(imManager)
                    }
                }
    }

    private fun initIMConnection(imManager: LTIMManager): Observable<LTIMManager> {
        return Observable
                .create<Boolean> { emitter ->
                    imManager.setManagerListener(mIMReceiver)
                    imManager.connect(object : LTCallbackResultListener<Boolean> {
                        override fun onResult(result: Boolean) {
                            emitter.onNext(true)
                            emitter.onComplete()
                        }

                        override fun onError(errorInfo: LTErrorInfo) {
                            emitter.onError(errorInfo)
                        }
                    })
                }
                .map {
                    FCMTokenHelper.performUpdate()
                    imManager
                }
                .doOnError {
                    Timber.tag(TAG).e("initIMConnection ++ error: $it")
                }
    }

    fun getStorageManager(receiverID: String?): Observable<LTStorageManager> {
        return sdkObservable
                .map { sdk: LTSDK -> sdk.getStorageManager(receiverID) }
    }

    fun getCallCenterManager(): Observable<LTCallCenterManager> {
        return sdkObservable
                .map { sdk: LTSDK -> sdk.getCallCenterManager() }
    }

    fun resetSDK(): Observable<Boolean> {
        return LTSDK.clean(SampleApp.context)
                .doOnNext {
                    Timber.tag(TAG).d("resetSDK status: $it")
                }
                .doOnError {
                    Timber.tag(TAG).e("resetSDK error: $it")
                }
    }
}