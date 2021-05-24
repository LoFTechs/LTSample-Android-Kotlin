package com.loftechs.sample.profile

import android.content.Intent
import com.loftechs.sample.base.BaseContract
import java.io.File

interface SetProfileContract {
    interface View : BaseContract.BaseView {
        fun gotoMainFragment()
        fun loadAvatar(file: File?)
        fun setNicknameText(nicknameText: String)
        fun dismissFragment()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun setNickname(nickname: String)
        fun setProfileImage(data: Intent?)
        fun hasAvatar(): Boolean
        fun deleteAvatar()
    }
}