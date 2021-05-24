package com.loftechs.sample.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.loftechs.sample.base.BaseContract

interface MainContract {
    interface View {
        fun gotoCreateChannel()
        fun gotoCreateCall()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        val tabItemCount: Int

        fun logout()

        @StringRes
        fun getTabStringResourceID(position: Int): Int
        fun getTabItemType(position: Int): MainItemType
        fun onFabClick(currentItem: Int)

        @DrawableRes
        fun getFabIconResource(position: Int): Int
    }
}