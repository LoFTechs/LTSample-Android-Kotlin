package com.loftechs.sample.call.voice

import android.net.Uri
import com.loftechs.sample.base.BaseContract

interface VoiceCallContract {
    interface View : BaseContract.BaseView {
        fun enableIncomingCallView()
        fun enableInCallView()
        fun refreshCallStatus()
        fun setNickname(nickname: String)
        fun setAvatar(uri: Uri?)
        fun finishView()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun selectCallByStatusType()
        fun startCallByUserID(receiverID: String, userID: String)
        fun acceptCall()
        fun hangup()
    }
}