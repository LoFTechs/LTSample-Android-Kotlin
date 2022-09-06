package com.loftechs.sample

import com.loftechs.sample.fcm.FCMTokenHelper
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sample.receiver.IMReceiver
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.LTSDKOptions
import com.loftechs.sdk.call.LTCallCenterManager
import com.loftechs.sdk.extension.rx.cleanData
import com.loftechs.sdk.extension.rx.deletePrimaryUser
import com.loftechs.sdk.extension.rx.init
import com.loftechs.sdk.http.response.LTResponse
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
                        Timber.tag(TAG).d("initIMConnection ++ success: $result")
                        emitter.onNext(true)
                        emitter.onComplete()
                    }

                    override fun onError(errorInfo: LTErrorInfo) {
                        Timber.tag(TAG).e("initIMConnection ++ error: $errorInfo")
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

    fun getStorageManager(receiverID: String): Observable<LTStorageManager> {
        return sdkObservable
            .map { sdk: LTSDK -> sdk.getStorageManager(receiverID) }
    }

    fun getCallCenterManager(): Observable<LTCallCenterManager> {
        return sdkObservable
            .map { sdk: LTSDK -> sdk.getCallManager() }
    }

    fun resetSDK(): Observable<Boolean> {
        return LTSDK.cleanData()
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
                            mSDK = LTSDK
                            mSDK
                        }
                } ?: return Observable.error(Throwable("$TAG error: no user in sample app"))
            }
            return Observable.just(mSDK)
        }

    fun deletePrimaryUser(): Observable<LTResponse> {
        return LTSDK.deletePrimaryUser()
            .doOnNext {
                Timber.tag(TAG).d("deletePrimaryUser ++ success: $it")
            }
            .doOnError {
                Timber.tag(TAG).e("deletePrimaryUser ++ error: $it")
            }
    }
}