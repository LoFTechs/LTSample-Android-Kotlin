package com.loftechs.sample.model.api

import com.loftechs.sample.LTSDKManager.sdkObservable
import com.loftechs.sample.fcm.FCMTokenHelper
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.user.LTUserStatus
import com.loftechs.sdk.user.LTUsers
import io.reactivex.Observable

object UserManager {

    fun getUserStatusWithSemiUIDs(accountList: List<String>): Observable<List<LTUserStatus>> {
        return sdkObservable
            .flatMap { sdk: LTSDK -> getUserStatusByRemote(sdk, accountList) }
    }

    fun getUsers(): Observable<LTUsers> {
        return Observable.create { emmit ->
            LTSDK.getUsers(object : LTCallbackResultListener<LTUsers> {
                override fun onError(errorInfo: LTErrorInfo) {
                    emmit.onError(errorInfo)
                }

                override fun onResult(result: LTUsers) {
                    FCMTokenHelper.performUpdate()
                    emmit.onNext(result)
                    emmit.onComplete()
                }

            })
        }
    }

    private fun getUserStatusByRemote(
        sdk: LTSDK,
        accountList: List<String>
    ): Observable<List<LTUserStatus>> {
        return Observable.create { emitter ->
            sdk.getUserStatusWithSemiUIDs(accountList,
                object : LTCallbackResultListener<List<LTUserStatus>> {
                    override fun onError(errorInfo: LTErrorInfo) {
                        emitter.onError(errorInfo)
                    }

                    override fun onResult(result: List<LTUserStatus>) {
                        emitter.onNext(result)
                        emitter.onComplete()
                    }
                })
        }
    }
}