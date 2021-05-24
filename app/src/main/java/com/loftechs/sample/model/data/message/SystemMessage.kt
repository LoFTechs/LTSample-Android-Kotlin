package com.loftechs.sample.model.data.message

import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

data class SystemMessage(
        val messageID: String,
        val systemMessage: String,
        val sender: User,
        val time: Date,
) : BaseMessage(messageID, systemMessage, sender, time), MessageContentType