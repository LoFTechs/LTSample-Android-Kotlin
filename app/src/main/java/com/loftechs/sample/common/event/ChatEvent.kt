package com.loftechs.sample.common.event

data class ChatEvent(
        val receiverID: String,
        val chID: String,
) {
    var subject: String = ""
    var memberCount: Int = -1
}