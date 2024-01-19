package com.loftechs.sample.call.voice

import android.annotation.SuppressLint
import android.os.Bundle
import com.loftechs.sample.call.list.CallState
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.CallManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.utils.LTLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class VoiceCallPresenter : VoiceCallContract.Presenter<VoiceCallContract.View> {

    private var mView: VoiceCallContract.View? = null

    lateinit var mReceiverID: String
    private lateinit var mCallUserID: String
    private var mCallState: Int = CallState.NONE.ordinal
    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun create() {
    }

    override fun resume() {
        mView?.refreshCallStatus()
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: VoiceCallContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
        mCallUserID = arguments.getString(IntentKey.EXTRA_CALL_USER_ID, "")
        mCallState = arguments.getInt(IntentKey.EXTRA_CALL_STATE_TYPE, CallState.NONE.ordinal)
        LTLog.i("VoiceCallPresenter","mCallState $mCallState")
    }

    override fun selectCallByStatusType() {
        if (mCallState == CallState.OUT.ordinal || mCallState == CallState.ACCEPT.ordinal) {
            mView?.enableInCallView()
        } else {
            mView?.enableIncomingCallView()
        }
        startCallByUserID(mReceiverID, mCallUserID)
    }

    @SuppressLint("CheckResult")
    override fun startCallByUserID(receiverID: String, userID: String) {
        ProfileInfoManager.getProfileInfoByUserID(mReceiverID, mCallUserID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("startCallByUserID : $it")
                    mView?.setNickname(it.displayName)
                    getAvatar(it.profileFileInfo)
                    if (mCallState == CallState.OUT.ordinal) {
                        startOutgoingCall(it)
                    }else if(mCallState == CallState.ACCEPT.ordinal){
                        acceptCall()
                    }
                }, {
                    logError("startCallByUserID", it)
                })
    }

    @SuppressLint("CheckResult")
    private fun startOutgoingCall(profileInfoEntity: ProfileInfoEntity) {
        CallManager.doOutgoingCallWithUserID(mReceiverID, profileInfoEntity.displayName, mCallUserID)
                .subscribe(
                        {
                            logDebug("startOutgoingCall to : $it")
                        },
                        { logError("startOutgoingCall", it) }
                )
    }

    private fun getAvatar(fileInfo: LTFileInfo?) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            mView?.setAvatar(it)
                        },
                        {
                            logError("bindAvatar", it)
                            mView?.setAvatar(null)
                        }
                )
        mDisposable.add(subscribe)
    }

    override fun acceptCall() {
        CallManager.doAccept()
    }

    override fun hangup() {
        CallManager.doHangup()
        mView?.finishView()
    }
}