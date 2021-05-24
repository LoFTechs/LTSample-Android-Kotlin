package com.loftechs.sample.chat.settings

import android.content.Intent
import android.net.Uri
import com.loftechs.sample.base.BaseContract

interface ChatSettingsContract {
    interface View : BaseContract.BaseView {
        fun getStringValue(resValue: Int): String
        fun setSubject(subject: String?)
        fun refreshList(settingsData: List<ChatSettingsData>)
        fun gotoMemberList()
        fun showEditSubjectDialog()
        fun showEditAvatarDialog()
        fun pickImage()
        fun loadAvatar(uri: Uri?, defaultDrawable: Int)
        fun finishChat()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun onBindViewHolder(view: ChatSettingsAdapter.IItemView, position: Int)
        fun onItemClick(view: ChatSettingsAdapter.IItemView, data: ChatSettingsData)
        fun requestData(userID: String, chID: String)
        fun updateChannelSubject(subject: String)
        fun editAvatar()
        fun deleteAvatar()
        fun setProfileImage(intent: Intent?)
        fun canEditAvatarOrSubject(): Boolean
    }
}