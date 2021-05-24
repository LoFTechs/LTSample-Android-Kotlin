package com.loftechs.sample.authentication.register

import android.os.Bundle
import com.loftechs.sample.base.BaseContract

interface RegisterContract {
    interface View : BaseContract.BaseView {
        fun gotoMainActivity(bundle: Bundle)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun register(account: String, password: String, confirmPassword: String)
    }
}