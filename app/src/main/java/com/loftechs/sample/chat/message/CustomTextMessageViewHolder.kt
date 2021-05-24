package com.loftechs.sample.chat.message

import android.view.View
import android.widget.TextView
import com.loftechs.sample.R
import com.loftechs.sample.model.data.message.TextMessage
import com.stfalcon.chatkit.messages.MessageHolders

class CustomTextMessageViewHolder(
        itemView: View,
        payload: Any?,
) : MessageHolders.IncomingTextMessageViewHolder<TextMessage>(itemView, payload) {

    private val mSenderNameTextView: TextView = itemView.findViewById(R.id.sender)

    override fun onBind(message: TextMessage) {
        super.onBind(message)
        mSenderNameTextView.text = if (message.sender.name.isEmpty()) {
            "Default Name"
        } else {
            message.sender.name
        }
    }
}