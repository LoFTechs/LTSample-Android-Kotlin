package com.loftechs.sample.chat.create.group

import android.content.Intent
import android.net.Uri
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.channels.LTChannelType
import java.util.*

interface CreateGroupContract {
    interface View : BaseContract.BaseView {
        fun refreshList(itemList: ArrayList<ProfileInfoEntity>)
        fun gotoChatPage(channelID: String?, channelType: LTChannelType, subject: String, memberCount: Int)
        fun loadAvatar(uri: Uri?)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun onBindViewHolder(view: CreateGroupAdapter.ISelectedItemView, position: Int)
        fun createGroup(subject: String)
        fun setProfileImage(intent: Intent?)
    }
}