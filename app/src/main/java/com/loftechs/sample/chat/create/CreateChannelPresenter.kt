package com.loftechs.sample.chat.create

import android.os.Bundle
import com.loftechs.sample.chat.create.CreateItemType.*
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.create.AbstractCreatePresenter
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.message.LTMemberModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class CreateChannelPresenter : AbstractCreatePresenter() {

    override val defaultItemList: ArrayList<ProfileInfoEntity> = arrayListOf(
            GROUP.getUserInfo(),
            NEW_CONTACT.getUserInfo()
    )

    override var itemList: ArrayList<ProfileInfoEntity> = ArrayList()
        get() {
            if (field.isEmpty()) {
                field.addAll(defaultItemList)
            }
            return field
        }

    override val disposable: CompositeDisposable
        get() = mDisposable

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun getItemType(position: Int): CreateItemType {
        return when (position) {
            0 -> {
                GROUP
            }
            1 -> {
                NEW_CONTACT
            }
            else -> {
                ITEM
            }
        }
    }

    override fun getUserInfoByPosition(position: Int): ProfileInfoEntity {
        return itemList[position]
    }

    override fun createOneToOne(userID: String, nickname: String): Observable<Bundle> {
        return ChatFlowManager.createSingleChannel(mReceiverID, LTMemberModel(userID))
                .map {
                    mArgument.putString(IntentKey.EXTRA_CHANNEL_ID, it.chID)
                    mArgument.putSerializable(IntentKey.EXTRA_CHANNEL_TYPE, it.chType)
                    mArgument.putString(IntentKey.EXTRA_CHANNEL_SUBJECT, nickname)
                    mArgument.putInt(IntentKey.EXTRA_CHANNEL_MEMBER_COUNT, it.members.size)
                    mArgument
                }
    }
}
