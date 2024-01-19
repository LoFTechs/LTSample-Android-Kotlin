package com.loftechs.sample.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loftechs.sample.model.api.CallManager
import com.loftechs.sdk.utils.LTLog

class CallNotificationDeclineCallIntentReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = CallNotificationDeclineCallIntentReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        LTLog.d(TAG, "onReceive CallNotificationDenyCallIntentReceiver")
        CallManager.doHangup()
    }
}