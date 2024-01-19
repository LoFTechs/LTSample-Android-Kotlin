package com.loftechs.sample.fcm

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.loftechs.sample.SampleApp
import com.loftechs.sample.utils.VersionUtil
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.LTSDKNoInitializationException
import com.loftechs.sdk.http.response.LTResponse
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

object FCMTokenHelper {
    private val TAG = FCMTokenHelper::class.java.simpleName

    fun performUpdate() {
        if (!shouldUpdateFCMKey()) {
            Timber.tag(TAG).d("performUpdate no need to update")
            return
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                handleKey(it.result.toString())
            }
        }
    }

    fun saveFCMKey(registrationId: String?) {
        val expired = Calendar.getInstance().timeInMillis + TimeUnit.DAYS.toMillis(3)
        FCMPrefManager.gcmExpire = expired
        FCMPrefManager.gcmKey = registrationId
    }

    private fun shouldUpdateFCMKey(): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        val expiryTime = FCMPrefManager.gcmExpire
        val isTimeExpired = currentTime > expiryTime
        val currentVersionCode = VersionUtil.getAppVersionCode(SampleApp.context)
        val savedVersionCode = FCMPrefManager.appVersionCode
        val isVersionUpdated = currentVersionCode > savedVersionCode
        if (isVersionUpdated) {
            FCMPrefManager.appVersionCode = currentVersionCode
        }
        val isInvalidKey = FCMPrefManager.gcmKey.isNullOrEmpty()
        return isTimeExpired || isVersionUpdated || isInvalidKey
    }

    private fun handleKey(registrationId: String?) {
        Timber.tag(TAG).d("handleKey registrationId = $registrationId")
        if (registrationId.isNullOrEmpty()) {
            val intent = Intent(SampleApp.context, MyFirebaseMessagingService::class.java)
            SampleApp.context.startService(intent)
            return
        }
        updateKeyWithServer(registrationId)
    }

    private fun updateKeyWithServer(registrationId: String?) {
        Timber.tag(TAG).d("Send key to server")
        try {
            LTSDK.updateNotificationKey(
                registrationId,
                false,
                object : LTCallbackResultListener<LTResponse> {
                    override fun onError(errorInfo: LTErrorInfo) {
                        Timber.tag(TAG).d("updateNotificationKey onError: $errorInfo")
                    }

                    override fun onResult(result: LTResponse) {
                        Timber.tag(TAG)
                            .d("updateNotificationKey returnCode: ${result.getReturnCode()}")
                        saveFCMKey(registrationId)
                    }
                })
        } catch (e: LTSDKNoInitializationException) {
            e.printStackTrace()
        }
    }
}