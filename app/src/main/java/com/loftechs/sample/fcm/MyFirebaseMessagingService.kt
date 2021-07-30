package com.loftechs.sample.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.model.api.CallManager
import com.loftechs.sdk.LTSDK
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FCMTokenHelper.performUpdate()
        Timber.tag(TAG).i("onNewToken ++ token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        checkJsonKey(remoteMessage)
    }

    private fun checkJsonKey(notification: RemoteMessage) {
        val keys: Set<String> = notification.data.keys
        for (s in keys) {
            if (s == "json") {
                handleFCMMessage(notification.data[s])
                break
            }
        }
    }

    private fun handleFCMMessage(messageJson: String?) {
        Timber.tag(TAG).i("handleFCMMessage ++ message: $messageJson")
        if (!isJson(messageJson)) {
            MyNotificationManager.pushNotify(messageJson)
            return
        }
        LTSDKManager.sdkObservable
                .subscribe(object : Observer<LTSDK> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(ltsdk: LTSDK) {
                        CallManager.parseFCMCallMessage(messageJson!!)
                    }

                    override fun onError(e: Throwable) {
                        // 非 call content json.
                        MyNotificationManager.pushNotify(messageJson)
                    }

                    override fun onComplete() {}
                })
    }

    private fun isJson(str: String?): Boolean {
        str?.let {
            val parse: Any
            parse = try {
                val jsonObject = JSONObject(it)
                val content = jsonObject.getString("content")
                JsonParser.parseString(content)
            } catch (e: Exception) {
                return false
            }
            return parse is JsonObject
        } ?: return false
    }
}