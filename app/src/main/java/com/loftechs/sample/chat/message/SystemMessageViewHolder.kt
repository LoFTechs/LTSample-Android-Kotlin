package com.loftechs.sample.chat.message

import android.view.View
import android.widget.TextView
import com.loftechs.sample.R
import com.loftechs.sample.model.data.message.SystemMessage
import com.stfalcon.chatkit.messages.MessageHolders

class SystemMessageViewHolder(
        itemView: View,
) : MessageHolders.BaseMessageViewHolder<SystemMessage>(itemView, null) {

    private var mSystemTextView: TextView = itemView.findViewById(R.id.messageText)

    override fun onBind(data: SystemMessage?) {
        mSystemTextView.let {
            it.text = data?.text
        }
    }
}
