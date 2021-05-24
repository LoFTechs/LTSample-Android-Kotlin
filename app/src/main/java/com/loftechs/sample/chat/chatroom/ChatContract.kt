package com.loftechs.sample.chat.chatroom

import android.content.Intent
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.data.message.BaseMessage
import java.io.File
import java.util.*

interface ChatContract {
    interface View : BaseContract.BaseView {
        fun refreshMessageList(messageList: ArrayList<BaseMessage>)
        fun refreshNewMessage(message: BaseMessage)
        fun clearMessages()
        fun showCallIconInToolbar(isVisible: Boolean)
        fun refreshSpecificMessage(message: BaseMessage)
        fun deleteMessage(message: BaseMessage)
        fun viewImage(file: File)
        fun displayTitle()
        fun finishChat()
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        val receiverID: String
        val subject: String
        val subtitle: String
        fun loadMessages()
        fun sendTextMessage(textToSend: String)
        fun sendImageMessage(intent: Intent?)
        fun messageClick(message: BaseMessage)
        fun recallMessage(message: BaseMessage)
        fun deleteMessage(message: BaseMessage)
        fun clearChatMessages()
        fun getLongClickMenuItemList(message: BaseMessage): Array<String>
        fun getUserIDFromOneToOne(): String
    }
}