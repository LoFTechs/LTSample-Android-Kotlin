package com.loftechs.sample.chat.message

import android.view.View
import android.widget.TextView
import com.loftechs.sample.R
import com.loftechs.sample.model.data.message.ImageMessage
import com.stfalcon.chatkit.messages.MessageHolders

class CustomImageMessageViewHolder(
        itemView: View,
        payload: Any?,
) : MessageHolders.IncomingImageMessageViewHolder<ImageMessage>(itemView, payload) {

    private val mSenderNameTextView: TextView = itemView.findViewById(R.id.sender)

    override fun onBind(message: ImageMessage) {
        super.onBind(message)
        mSenderNameTextView.text = if (message.sender.name.isEmpty()) {
            "Default Name"
        } else {
            message.sender.name
        }
    }
}