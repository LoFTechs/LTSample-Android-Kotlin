package com.loftechs.sample.common.event

data class ChatCloseEvent(
        var receiverID: String,
        var chID: String,
)