package com.loftechs.sample.call.list

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.CallManager
import com.loftechs.sample.utils.DateFormatUtil
import com.loftechs.sdk.call.core.LTCallState
import com.loftechs.sdk.storage.LTFileInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class CallListPresenter : CallListContract.Presenter<CallListContract.View> {

    private var mView: CallListContract.View? = null

    private lateinit var mReceiverID: String

    private val mCallLogList: ArrayList<CallLogData> by lazy {
        ArrayList()
    }

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    companion object {
        private const val DEFAULT_COUNT = 20
    }

    override fun create() {
    }

    override fun resume() {
        if (mCallLogList.isEmpty()) {
            getCallLog(System.currentTimeMillis(), -DEFAULT_COUNT)
        }
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: CallListContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun getReceiverID(): String {
        return mReceiverID
    }

    override fun onBindViewHolder(view: CallListAdapter.IItemView, position: Int) {
        val callLogData = mCallLogList[position]
        view.setStartTime(DateFormatUtil.getStringFormat(callLogData.startTime, "MM/dd HH:mm:ss"))
        view.setState(callLogData.callState)
        val subscribe = ProfileInfoManager.getProfileInfoByUserID(mReceiverID, getCallUserID(callLogData))
                .doOnNext {
                    bindAvatar(view, it.profileFileInfo)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.setDisplayName(it.displayName)
                }, {
                    logError("onBindViewHolder", it)
                })
        mDisposable.add(subscribe)
    }

    private fun bindAvatar(view: CallListAdapter.IItemView, fileInfo: LTFileInfo?) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.bindAvatar(it)
                }, {
                    logError("bindAvatar", it)
                    view.bindAvatar(null)
                })
        mDisposable.add(subscribe)
    }

    private fun getCallUserID(callLogData: CallLogData): String {
        var callUserID = callLogData.caller.userID
        if (isSelfID(callUserID)) {
            callUserID = callLogData.callee.userID
        }
        return callUserID
    }

    override fun getCallLog(requestTime: Long, count: Int) {
        val subscribe = CallManager.getCallLog(mReceiverID, requestTime, count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("getCallLog : ${it.size}")
                    if (it.isNotEmpty()) {
                        mCallLogList.addAll(it)
                        mView?.addData(it)
                        // load more
                        if (it.size == DEFAULT_COUNT) {
                            getCallLog(it.last().startTime - 1, -DEFAULT_COUNT)
                        }
                    }
                }, {
                    logError("getCallLog", it)
                    mView?.showSnackBar(R.string.call_list_get_call_log_list_error)
                })
        mDisposable.add(subscribe)
    }

    private fun isSelfID(userID: String): Boolean {
        return userID == mReceiverID
    }

    override fun executeCall(callLogData: CallLogData) {
        if (CallManager.callState == LTCallState.IDLE) {
            mView?.gotoVoiceCall(mReceiverID, getCallUserID(callLogData))
        } else {
            mView?.showSnackBar(R.string.call_list_duplicate_call)
        }
    }
}