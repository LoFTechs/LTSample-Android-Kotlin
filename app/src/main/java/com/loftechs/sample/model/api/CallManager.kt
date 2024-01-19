package com.loftechs.sample.model.api

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.LTSDKManager.sdkObservable
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.call.CallNotificationDeclineCallIntentReceiver
import com.loftechs.sample.call.list.CallLogData
import com.loftechs.sample.call.list.CallState
import com.loftechs.sample.call.voice.VoiceCallActivity
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.CallCDREvent
import com.loftechs.sample.common.event.CallStateChangeEvent
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.call.*
import com.loftechs.sdk.call.LTCallOptions.UserIDBuilder
import com.loftechs.sdk.call.api.LTAllowListResponse
import com.loftechs.sdk.call.api.LTBlockListResponse
import com.loftechs.sdk.call.api.LTSetAllowResponse
import com.loftechs.sdk.call.api.LTSetBlockResponse
import com.loftechs.sdk.call.core.LTCall
import com.loftechs.sdk.call.core.LTCallState
import com.loftechs.sdk.call.core.LTMediaType
import com.loftechs.sdk.call.message.LTCallCDRNotificationMessage
import com.loftechs.sdk.call.message.LTCallNotificationMessage
import com.loftechs.sdk.call.notification.NotificationCenter
import com.loftechs.sdk.call.route.LTAudioRoute
import com.loftechs.sdk.http.response.LTResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException
import java.util.*

object CallManager : LTCallStateListener, LTCallNotificationListener {
    private val TAG = CallManager::class.java.simpleName
    var ltCall: LTCall? = null
    private var audioManager: AudioManager
    private var ringerPlayer: MediaPlayer? = null
    private var vibrator: Vibrator?
    private var isRinging = false
    private var ltCallCenterManager: LTCallCenterManager = LTSDK.getCallManager()
    var majorCallID: String? = null

    init {
        logDebug("CallManager ++")
        audioManager = SampleApp.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = when (Build.VERSION.SDK_INT) {
            31 -> (SampleApp.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            else -> SampleApp.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        getCallManager()
            .map {
                it.callNotificationListener = this@CallManager
                true
            }
            .subscribe()
        resetStatus()
        logDebug("CallManager--")
    }

    private fun getCallManager(): Observable<LTCallCenterManager> {
        return LTSDKManager.getCallCenterManager()
            .doOnError {
                Timber.tag(TAG).e("init callCenterManager Exception: ${it.message}")
            }
    }

    fun parseFCMCallMessage(messageJson: String) {
        sdkObservable
            .subscribe(object : Observer<LTSDK> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(ltsdk: LTSDK) {
                    logDebug("parseFCMCallMessage messageJson : $messageJson")
                    ltsdk.parseIncomingPushWithNotify(messageJson)
                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })
    }

    fun doOutgoingCallWithUserID(
        receiverID: String,
        account: String,
        userID: String,
    ): Observable<Boolean> {
        logDebug("doOutgoingCallWithUserID user: $userID, callCount: ${ltCallCenterManager.activeCallCount}")
        if (ltCall != null) {
            return Observable.just(false)
        }
        val callOptions = UserIDBuilder()
            .setUserID(userID)
            .build()
        logDebug("getNumberOfCalls: ${ltCallCenterManager.activeCallCount}")
        return getCallManager()
            .flatMap {
                logDebug("start a outgoing $it")
                val builder = setNotificationBuilder(receiverID, userID, "", account, false)
                ltCall = it.startCallWithUserID(
                    receiverID,
                    callOptions,
                    this@CallManager,
                    builder,
                    LTCallCenterManager.NOTIFY_CALL_IN_APP
                )
                ltCall?.let { call ->
                    majorCallID = call.callID
                }
                Observable.just(true)
            }
            .doOnError {
                Timber.tag(TAG).e("getCallCenterManager error: $it")
            }
    }

    private fun doIncomingCall(incomingCallMessage: LTCallNotificationMessage) {
        if (!incomingCallMessage.isIncomingCallMessage) {
            Timber.tag(TAG).e("Not IncomingCall Message")
            return
        }
        getCallManager()
            .subscribe(object : Observer<LTCallCenterManager> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(centerManager: LTCallCenterManager) {
                    val number = when (incomingCallMessage.callOptions.semiUID.isNullOrEmpty()) {
                        true -> incomingCallMessage.callOptions.phoneNumber
                        else -> incomingCallMessage.callOptions.semiUID
                    }
                    val builder = setNotificationBuilder(
                        incomingCallMessage.receiver,
                        incomingCallMessage.callOptions.userID,
                        "",
                        number
                            ?: "",
                        true
                    )
                    val tempIncomingCall = centerManager.startCallWithNotificationMessage(
                        incomingCallMessage,
                        this@CallManager,
                        builder,
                        LTCallCenterManager.NOTIFY_CALL_IN_APP
                    )
                    if (tempIncomingCall != null) {
                        logDebug("getNumberOfCalls: ${centerManager.activeCallCount}")
                        if (centerManager.activeCallCount >= 2 || ltCall != null) {
                            logDebug("busyCall callID: ${tempIncomingCall.callID}")
                            tempIncomingCall.busyCall()
                            return
                        }
                        ltCall = tempIncomingCall
                        majorCallID = ltCall?.callID
                        startCallActivity(
                            getCallIntent(
                                incomingCallMessage.receiver,
                                incomingCallMessage.callOptions.userID,
                                CallState.IN
                            )
                        )
                        logDebug("start a incoming call: ${ltCall?.callID}")
                        startRinging()
                    }
                }

                override fun onError(e: Throwable) {
                    Timber.tag(TAG).e("getCallCenterManager error: $e")
                }

                override fun onComplete() {}
            })
    }

    fun startCallActivity(intent: Intent) {
        SampleApp.context.startActivity(intent)
    }

    private fun getCallIntent(
        receiverID: String,
        callUserID: String,
        callState: CallState,
    ): Intent {
        val intent = Intent().addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        intent.setClass(SampleApp.context, VoiceCallActivity::class.java)
        intent.putExtra(IntentKey.EXTRA_RECEIVER_ID, receiverID)
        intent.putExtra(IntentKey.EXTRA_CALL_USER_ID, callUserID)
        intent.putExtra(IntentKey.EXTRA_CALL_STATE_TYPE, callState.ordinal)
        return intent
    }

    val callState: LTCallState
        get() {
            return ltCall?.ltCallState ?: LTCallState.IDLE
        }

    fun doAccept() {
        ltCall?.acceptCall()
    }

    fun doHangup() {
        ltCall?.hangupCall()
    }

    var isCallMuted: Boolean
        get() = ltCall?.isCallMuted ?: false
        set(mute) {
            ltCall?.isCallMuted = mute
        }

    var isCallHeld: Boolean
        get() = ltCall?.isCallHeld ?: false
        set(hold) {
            ltCall?.isCallHeld = hold
        }

    val isSpeakerOn: Boolean
        get() = ltCall?.speakerStatus ?: false

    fun routeAudioToSpeaker() {
        ltCall?.setAudioRoute(LTAudioRoute.LTAudioRouteSpeaker)
    }

    fun routeAudioToReceiver() {
        ltCall?.setAudioRoute(LTAudioRoute.LTAudioRouteBuiltin)
    }

    fun routeAudioToBluetooth() {
        ltCall?.setAudioRoute(LTAudioRoute.LTAudioRouteBluetooth)
    }

    fun getCallLog(
        receiverID: String,
        startTime: Long,
        count: Int,
    ): Observable<ArrayList<CallLogData>> {
        return ltCallCenterManager.queryCDRWithUserID(receiverID, startTime, count)
            .filter { !it.cdrMessages.isNullOrEmpty() }
            .flatMapIterable { it.cdrMessages }
            .map {
                CallLogData(
                    it.senderID,
                    it.callID,
                    it.callStartTime,
                    it.callEndTime,
                    it.calleeInfo,
                    it.callerInfo,
                    it.billingSecond,
                    getCallType(receiverID, it.callerInfo.userID, it.billingSecond.toLong())
                )
            }
            .collect(
                { ArrayList() },
                BiConsumer(ArrayList<CallLogData>::add) as BiConsumer<ArrayList<CallLogData>, CallLogData>
            )
            .toObservable()
    }

    private fun getCallType(receiverID: String, userID: String, billingSecond: Long): CallState {
        return when {
            userID == receiverID -> {
                CallState.OUT
            }

            billingSecond == 0L -> {
                CallState.MISS
            }

            else -> {
                CallState.IN
            }
        }
    }

    fun adjustVolume(i: Int) {
        try {
            if (isRinging) {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_RING,
                    if (i < 0) AudioManager.ADJUST_LOWER else AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
            } else {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    if (i < 0) AudioManager.ADJUST_LOWER else AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
            }
        } catch (e: Exception) {
            Timber.tag(TAG).w("adjustVolume Exception: ${e.message}")
        }
    }

    private fun resetStatus() {
        ltCall = null
    }

    @Synchronized
    private fun startVibrateIfNeeds() {
        try {
            val canVibrate = when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_VIBRATE -> true
                AudioManager.RINGER_MODE_NORMAL,
                -> Settings.System.getInt(
                    SampleApp.context.contentResolver,
                    Settings.System.VIBRATE_WHEN_RINGING,
                    0
                ) == 1

                else -> false
            }
            if (!canVibrate) {
                return
            }
            val pattern = longArrayOf(0, 1000, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun startRinging() {
        try {
            startVibrateIfNeeds()
            ringerPlayer?.let {
                Timber.i("already ringing")
            } ?: run {
                ringerPlayer = MediaPlayer()
                ringerPlayer?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                ringerPlayer?.let {
                    onRingerPlayerCreated(it)
                    it.prepare()
                    it.isLooping = true
                    it.start()
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e("cannot handle incoming call : $e")
        }
        isRinging = true
    }

    @Synchronized
    private fun stopRinging() {
        ringerPlayer?.let {
            it.stop()
            it.release()
            ringerPlayer = null
        }
        vibrator?.cancel()
        isRinging = false
    }

    private fun onRingerPlayerCreated(mediaPlayer: MediaPlayer) {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        try {
            mediaPlayer.setDataSource(SampleApp.context, ringtoneUri)
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.tag(TAG).e("cannot set ringtone")
        }
    }

    @SuppressLint("WrongConstant")
    private fun setNotificationBuilder(
        receiverID: String,
        callUserID: String,
        content: String,
        displayName: String,
        isIncomingCall: Boolean,
    ): NotificationCompat.Builder? {
        var notifyMessage = content
        val appName =
            SampleApp.context.applicationInfo.loadLabel(SampleApp.context.packageManager).toString()
        var builder: NotificationCompat.Builder? = null
        var callState = if (isIncomingCall) CallState.IN else CallState.OUT
        if (notifyMessage.isEmpty()) {
            notifyMessage = if (isIncomingCall) " : Incoming Call" else " : Call Out"
        } else {
            callState = CallState.IN
        }
        try {
            val activityIntent = getCallIntent(receiverID, callUserID, callState)
            // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE
            val notificationContentIntent = if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.getActivity(
                    LTSDK.context, 0, activityIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    LTSDK.context, 0, activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            val smallRemoteViews =
                RemoteViews(SampleApp.context.packageName, R.layout.remote_view_small)
            val bigRemoteViews =
                RemoteViews(SampleApp.context.packageName, R.layout.remote_view_big)
            if (isIncomingCall) {
                smallRemoteViews.setTextViewText(R.id.txtTitle, displayName)
                smallRemoteViews.setTextViewText(R.id.txtMsg, notifyMessage)
                bigRemoteViews.setTextViewText(R.id.txtTitle, displayName)
                bigRemoteViews.setTextViewText(R.id.txtMsg, notifyMessage)
                val acceptIntent = getCallIntent(receiverID, callUserID, CallState.ACCEPT)
                // Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE
                val acceptContentIntent = if (Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getActivity(
                        LTSDK.context, 0, acceptIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
                    )
                } else {
                    PendingIntent.getActivity(
                        LTSDK.context, 0, acceptIntent,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                }

                val declineCallIntent = Intent(
                    LTSDK.context,
                    CallNotificationDeclineCallIntentReceiver::class.java
                )
                declineCallIntent.action = "call.notification.declinecall"

                val pendingDeclineCallIntent: PendingIntent
                if (Build.VERSION.SDK_INT >= 31) {
                    pendingDeclineCallIntent = PendingIntent.getBroadcast(
                        LTSDK.context,
                        0,
                        declineCallIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    pendingDeclineCallIntent = PendingIntent.getBroadcast(
                        LTSDK.context, 0, declineCallIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                bigRemoteViews.setOnClickPendingIntent(R.id.btnOK, acceptContentIntent)
                bigRemoteViews.setOnClickPendingIntent(R.id.btnCancel, pendingDeclineCallIntent)
                smallRemoteViews.setOnClickPendingIntent(R.id.btnOK, acceptContentIntent)
                smallRemoteViews.setOnClickPendingIntent(R.id.btnCancel, pendingDeclineCallIntent)
            }

            builder = getNotificationBuilder(
                "chID" + appName + "LP",
                "chName" + appName + "LP",
                NotificationManager.IMPORTANCE_MAX
            )
            builder.setSmallIcon(R.drawable.notif_call)
                .setAutoCancel(false)
                .setColor(ContextCompat.getColor(SampleApp.context, R.color.colorDefaultTheme))
                .setSound(null)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentTitle(appName)
                .setContentText(displayName + notifyMessage)
                .setContentIntent(notificationContentIntent)

            if (isIncomingCall) {
                builder.setCustomBigContentView(bigRemoteViews)
                builder.setCustomContentView(smallRemoteViews)
                //安卓12以上不用设置setCustomHeadsUpContentView
                if (Build.VERSION.SDK_INT < 31) {
                    builder.setCustomHeadsUpContentView(bigRemoteViews)//设置悬浮通知的样式
                }
            }
        } catch (exc: Exception) {
            logDebug("customNotificationChannel error $exc")
        }
        return builder
    }

    /**
     * Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel or it will not appear.
     * By categorizing notifications into channels, users can disable specific notification channels for your app (instead of disabling all your notifications),
     * and users can control the visual and auditory options for each channel—all from the Android system settings.
     */
    private fun getNotificationBuilder(
        channelID: String,
        channelName: String,
        importance: Int,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(SampleApp.context, channelID)
    }

    private fun isValidLTCallManager(call: LTCall?, callStatusCode: LTCallStatusCode): Boolean {
        if (call == null) {
            logDebug("isValidLTCallManager call null")
            return false
        }
        if (call.callID.isNullOrEmpty()) {
            return false
        }
        if (call.isGroupCall && majorCallID.isNullOrEmpty()) {
            majorCallID = call.callID
        }
        return call.callID == majorCallID
    }

    override fun onLTCallStateRegistered(call: LTCall) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return
        }
        logDebug("onLTCallStateRegistered callID: ${call.callID}, type: ${call.callType}")
        call.setAudioRoute(LTAudioRoute.LTAudioRouteBluetooth) // bluetooth優先
    }

    override fun onLTCallStateConnected(call: LTCall) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return
        }
        logDebug("onLTCallStateConnected callID: ${call.callID}")
        stopRinging()

        val builder = setNotificationBuilder(
            ltCallCenterManager.mainUserID,
            call.callOptions.userID,
            "In Call",
            "",
            false
        )
        NotificationCenter.getInstance()
            .updateAndroidCallNotification(builder, LTCallCenterManager.NOTIFY_CALL_IN_APP)
    }

    override fun onLTCallStateTerminated(call: LTCall, callStatusCode: LTCallStatusCode) {
        if (!isValidLTCallManager(call, callStatusCode)) {
            return
        }
        logDebug("onLTCallStateTerminated callID: ${call.callID}, callStatusCode: $callStatusCode")
        stopRinging()
        EventBus.getDefault().post(CallStateChangeEvent(callStatusCode, -1))
        //release major call
        ltCall = null
        majorCallID = ""
    }

    override fun onLTCallStateWarning(warnedCall: LTCall, callStatusCode: LTCallStatusCode) {
        logDebug("onLTCallStateWarning: callID: ${warnedCall.callID}, callStatusCode: $callStatusCode")
    }

    override fun onLTCallMediaStateChanged(mediaChangedCall: LTCall, mediaType: LTMediaType) {
        logDebug("onLTMediaStateChange: callID: ${mediaChangedCall.callID}, mediaType: $mediaType")
    }

    override fun onLTCallConnectionDuration(call: LTCall, duration: Int) {
        if (!isValidLTCallManager(call, LTCallStatusCode.NULL)) {
            return
        }
        if (call.callID == majorCallID) {
            EventBus.getDefault().post(CallStateChangeEvent(null, duration))
        }
    }

    override fun onLTCallNotification(callNotificationMessage: LTCallNotificationMessage) {
        doIncomingCall(callNotificationMessage)
    }

    override fun onLTCallCDRNotification(callCDRNotificationMessage: LTCallCDRNotificationMessage) {
        logDebug("onLTCallCDRNotification : $callCDRNotificationMessage")
        EventBus.getDefault().post(
            CallCDREvent(
                callCDRNotificationMessage.receiver,
                CallLogData(
                    callCDRNotificationMessage.senderID,
                    callCDRNotificationMessage.callID,
                    callCDRNotificationMessage.callStartTime,
                    callCDRNotificationMessage.callEndTime,
                    callCDRNotificationMessage.calleeInfo,
                    callCDRNotificationMessage.callerInfo,
                    callCDRNotificationMessage.billingSecond,
                    getCallType(
                        callCDRNotificationMessage.receiver,
                        callCDRNotificationMessage.callerInfo.userID,
                        callCDRNotificationMessage.billingSecond.toLong()
                    )
                )
            )
        )
    }

    //region block list
    fun getBlockList(receiverID: String): Observable<LTBlockListResponse> {
        return ltCallCenterManager.getBlockCallListWithUserID(receiverID)
    }

    fun setBlockList(
        receiverID: String,
        userIDs: ArrayList<String>,
    ): Observable<LTSetBlockResponse> {
        return ltCallCenterManager.blockCallWithUserID(receiverID, userIDs)
    }

    fun deleteBlockList(receiverID: String, userIDs: ArrayList<String>): Observable<LTResponse> {
        return ltCallCenterManager.unBlockCallWithUserID(receiverID, userIDs)
    }
    //endregion

    //region white list
    fun getAllowList(receiverID: String): Observable<LTAllowListResponse> {
        return ltCallCenterManager.getAllowListWithUserID(receiverID)
    }

    fun setAllowList(
        receiverID: String,
        userIDs: ArrayList<String>,
    ): Observable<LTSetAllowResponse> {
        return ltCallCenterManager.setAllowListWithUserID(receiverID, userIDs)
    }

    fun deleteAllowList(receiverID: String, userIDs: ArrayList<String>): Observable<LTResponse> {
        return ltCallCenterManager.deleteAllowListWithUserID(receiverID, userIDs)
    }
    //endregion
}