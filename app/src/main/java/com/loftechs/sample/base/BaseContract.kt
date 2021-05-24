package com.loftechs.sample.base

import android.os.Bundle
import androidx.annotation.StringRes

interface BaseContract {
    interface BaseView {
        fun showSnackBar(@StringRes messageResourceID: Int)
        fun showErrorDialog(@StringRes messageResourceID: Int)
        fun showProgressDialog()
        fun dismissProgressDialog()
    }

    interface BasePresenter {
        fun initBundle(arguments: Bundle)
        fun create()
        fun resume()
        fun pause()
        fun destroy()
    }

    interface Presenter<T> : BasePresenter {
        fun bindView(view: T)
        fun unbindView()
    }
}