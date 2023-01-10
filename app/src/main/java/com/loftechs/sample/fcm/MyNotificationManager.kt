package com.loftechs.sample.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.extensions.getShowMessage
import com.loftechs.sample.main.MainActivity
import com.loftechs.sample.model.PreferenceSetting
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.im.message.LTMessageType
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

object MyNotificationManager {

    private val TAG = MyNotificationManager::class.java.simpleName
    private const val CHANNEL_ID = "LT_EXAMPLE_PUSH"
    private const val NOTIFY_ID = 2021

    fun pushNotify(content: String?) {
        content ?: return
        val mute = PreferenceSetting.notificationMute
        if (mute) {
            Timber.tag(TAG).d("pushNotify is mute!")
            return
        }
        val notificationIntent = Intent(SampleApp.context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            val jsonObject = JSONObject(content)
            val enableContent = PreferenceSetting.notificationContent
            val enableDisplay = PreferenceSetting.notificationDisplay
            val messageType = jsonObject.getInt("type")
            var contentString = jsonObject.getString("content")
            val display = jsonObject.getString("dispName")
            contentString = if (!enableDisplay) { // 內容不顯示
                "You have a new message"
            } else if (!enableContent) {
                "$display : Sent a message "
            } else {
                "$display : ${getLastMessage(LTMessageType.create(messageType), contentString)}"
            }

            // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE
            val notificationContentIntent = if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.getActivity(SampleApp.context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(SampleApp.context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val builder = NotificationCompat.Builder(SampleApp.context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(ContextCompat.getColor(SampleApp.context, R.color.colorPrimaryDark))
                    .setContentTitle(SampleApp.context.getString(R.string.app_name))
                    .setContentText(contentString)
                    .setContentIntent(notificationContentIntent)
                    .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(SampleApp.context)
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance)
                channel.setShowBadge(true)
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel)
            } else {
                builder.priority = NotificationCompat.PRIORITY_MAX
            }
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NOTIFY_ID, builder.build())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getLastMessage(messageType: LTMessageType, content: String): String {
        return when (messageType) {
            LTMessageType.create(91), //91 邀請加入（chType=4）群組
            -> {
                SampleApp.context.resources.getString(R.string.chat_message_join)
            }
            LTMessageType.TYPE_IMAGE,
            LTMessageType.TYPE_RECALL_MESSAGE,
            LTMessageType.TYPE_ANSWER_INVITATION,
            LTMessageType.TYPE_CREATE_CHANNEL,
            LTMessageType.TYPE_INVITE_MEMBER,
            LTMessageType.TYPE_KICK_MEMBERS,
            LTMessageType.TYPE_LEAVE_CHANNEL,
            LTMessageType.TYPE_DISMISS_CHANNEL,
            LTMessageType.TYPE_SET_CHANNEL_PROFILE
            -> {
                messageType.getShowMessage()
            }
            else -> {
                content
            }
        }
    }
}