package com.loftechs.sample.profile

import android.os.Bundle
import androidx.annotation.StringRes
import com.loftechs.sample.base.BaseContract
import java.io.File

interface SettingContract {

    interface View : BaseContract.BaseView {
        fun setDefaultAvatarView()
        fun setAvatarView(avatarFile: File)
        fun setNicknameText(nicknameText: String)
        fun setMuteTitleText(@StringRes textResourceID: Int)
        fun setMuteStatus(isMute: Boolean)
        fun setDisplaySenderTitleText(@StringRes textResourceID: Int)
        fun setDisplaySenderStatus(showDisplaySender: Boolean)
        fun setDisplayContentTitleText(@StringRes textResourceID: Int)
        fun setDisplayContentStatus(showDisplayContent: Boolean)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun getSetProfileBundle(bundle: Bundle): Bundle
        fun setMute(enable: Boolean)
        fun enableNotificationDisplay(showSender: Boolean, showContent: Boolean)
    }
}