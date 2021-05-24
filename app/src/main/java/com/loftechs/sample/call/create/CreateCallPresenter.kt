package com.loftechs.sample.call.create

import android.os.Bundle
import com.loftechs.sample.call.list.CallState
import com.loftechs.sample.chat.create.CreateItemType
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.create.AbstractCreatePresenter
import com.loftechs.sample.model.data.ProfileInfoEntity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class CreateCallPresenter : AbstractCreatePresenter() {

    override val defaultItemList: ArrayList<ProfileInfoEntity> = arrayListOf(
            CreateItemType.NEW_CONTACT.getUserInfo()
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
                CreateItemType.NEW_CONTACT
            }
            else -> {
                CreateItemType.ITEM
            }
        }
    }

    override fun getUserInfoByPosition(position: Int): ProfileInfoEntity {
        return itemList[position]
    }

    override fun createOneToOne(userID: String, nickname: String): Observable<Bundle> {
        return Observable.create {
            mArgument.putString(IntentKey.EXTRA_CALL_USER_ID, userID)
            mArgument.putInt(IntentKey.EXTRA_CALL_STATE_TYPE, CallState.OUT.ordinal)
            it.onNext(mArgument)
            it.onComplete()
        }
    }
}