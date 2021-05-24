package com.loftechs.sample.chat.member

import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.data.MemberEntity

interface MemberListContract {
    interface View : BaseContract.BaseView {
        fun refreshList(members: List<MemberEntity>)
        fun showRemoveMemberDialog(channelName: String, displayName: String, id: String)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun onBindViewHolder(view: MemberListAdapter.IItemView, position: Int)
        fun requestRemoveMember(id: String, displayName: String)
        fun inviteMember(accountID: String)
        fun refreshData(chID: String)
        fun kickMember(id: String)
    }
}