package com.loftechs.sample.call.list

import com.loftechs.sdk.call.message.LTCallUserInfo

data class CallLogData(
        val senderID: String,
        val callID: String,
        val startTime: Long,
        val endTime: Long,
        val callee: LTCallUserInfo,
        val caller: LTCallUserInfo,
        val billingSecond: Double,
        val callState: CallState,
)
