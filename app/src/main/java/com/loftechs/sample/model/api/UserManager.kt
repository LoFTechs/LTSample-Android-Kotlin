package com.loftechs.sample.model.api

import com.loftechs.sample.LTSDKManager.sdkObservable
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.user.LTUserStatus
import io.reactivex.Observable

object UserManager {

    fun getUserStatusWithSemiUIDs(accountList: List<String>): Observable<List<LTUserStatus>> {
        return sdkObservable
                .flatMap { sdk: LTSDK -> sdk.getUserStatusWithSemiUIDs(accountList) }
    }
}