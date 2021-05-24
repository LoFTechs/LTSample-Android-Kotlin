package com.loftechs.sample.authentication

import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.R
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AccountHelper
import com.loftechs.sample.model.data.AccountEntity
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.user.LTUsers
import io.reactivex.Observable
import io.reactivex.functions.Function

abstract class AbstractAuthenticationPresenter {

    fun init(accountEntity: AccountEntity): Observable<LTUsers> {
        AccountHelper.setAccountEntity(accountEntity)
        return LTSDKManager.sdkObservable
                .doOnNext {
                    logDebug("init doOnNext ++")
                }
                .onErrorResumeNext(Function { e ->
                    if (e is LTErrorInfo && e.errorCode == LTErrorInfo.ErrorCode.INIT_ERROR && e.returnCode == 6000) {
                        LTSDKManager.resetSDK()
                                .flatMap { LTSDKManager.sdkObservable }
                    } else {
                        Observable.error(e)
                    }
                })
                .flatMap {
                    LTSDK.getInstance().users
                }
                .doOnError {
                    logError("init", it)
                }
    }

    fun parseErrorMessage(error: String): Int {
        return when (error) {
            "6" -> {
                R.string.error_input
            }
            "601" -> {
                R.string.error_password
            }
            "602" -> {
                R.string.error_account_not_exist
            }
            "603" -> {
                R.string.error_account_exists
            }
            "604" -> {
                R.string.error_need_password
            }
            else -> {
                R.string.error_unknown
            }
        }
    }
}