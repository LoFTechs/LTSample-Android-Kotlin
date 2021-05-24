package com.loftechs.sample.model.data.message

import java.util.*

data class TextMessage(
        val messageID: String,
        val messageContent: String,
        val sender: User,
        val time: Date,
) : BaseMessage(messageID, messageContent, sender, time)