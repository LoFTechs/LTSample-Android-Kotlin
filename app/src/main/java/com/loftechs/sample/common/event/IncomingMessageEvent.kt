package com.loftechs.sample.common.event

import com.loftechs.sdk.im.message.LTMessageResponse

data class IncomingMessageEvent(
        var receiverID: String,
        var channelID: String,
        var response: LTMessageResponse,
)