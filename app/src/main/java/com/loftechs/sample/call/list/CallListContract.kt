package com.loftechs.sample.call.list

import com.loftechs.sample.base.BaseContract

interface CallListContract {
    interface View : BaseContract.BaseView {
        fun addData(data: Any)
        fun gotoVoiceCall(receiverID: String, callUserID: String)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun getReceiverID(): String
        fun onBindViewHolder(view: CallListAdapter.IItemView, position: Int)
        fun getCallLog(requestTime: Long, count: Int)
        fun executeCall(callLogData: CallLogData)
    }
}