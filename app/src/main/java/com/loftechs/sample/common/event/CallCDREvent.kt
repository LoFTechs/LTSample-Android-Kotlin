package com.loftechs.sample.common.event

import com.loftechs.sample.call.list.CallLogData

data class CallCDREvent(
        val receiverID: String,
        val callLogData: CallLogData,
)
