package com.loftechs.sample.common.event

import androidx.annotation.Nullable
import com.loftechs.sdk.call.LTCallStatusCode

data class CallStateChangeEvent(
        @Nullable
        val state: LTCallStatusCode?,
        val duration: Int,
)
