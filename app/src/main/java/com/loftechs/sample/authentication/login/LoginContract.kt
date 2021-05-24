package com.loftechs.sample.authentication.login

import android.os.Bundle
import com.loftechs.sample.base.BaseContract

interface LoginContract {

    interface View : BaseContract.BaseView {
        fun gotoMainActivity(bundle: Bundle)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun login(account: String, password: String)
    }
}