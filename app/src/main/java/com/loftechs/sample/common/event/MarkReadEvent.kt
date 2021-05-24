package com.loftechs.sample.common.event

data class MarkReadEvent(
        val receiverID: String,
        val channelID: String,
)
