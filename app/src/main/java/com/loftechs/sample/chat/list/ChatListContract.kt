package com.loftechs.sample.chat.list

import com.loftechs.sample.base.BaseContract
import com.loftechs.sdk.im.channels.LTChannelResponse
import com.loftechs.sdk.im.channels.LTChannelType

interface ChatListContract {
    interface View {
        fun refreshChatList(items: List<LTChannelResponse>)
        fun gotoChatPage(channelID: String, channelType: LTChannelType, subject: String, memberCount: Int, lastMsgTime: Long)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        val receiverID: String

        fun onItemClick(response: LTChannelResponse, subject: String)
        fun loadChatList()
        fun onBindViewHolder(view: ChatListAdapter.IItemView, position: Int)
    }
}