package com.loftechs.sample.common.picker

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.channels.LTChannelType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PickerPresenter : PickerContract.Presenter<PickerContract.View> {

    private var mView: PickerContract.View? = null

    private lateinit var mReceiverID: String
    private lateinit var mUserInfoList: ArrayList<ProfileInfoEntity>
    private val mSelectedList: ArrayList<ProfileInfoEntity> by lazy {
        ArrayList()
    }

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun create() {
    }

    override fun resume() {
        mView?.refreshList(mUserInfoList)
        mSelectedList.clear()
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: PickerContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun onBindViewHolder(view: PickerAdapter.IPickerItemView, position: Int) {
        val profile = mUserInfoList[position]
        view.setTitleText(profile.displayName)
        val subscribe = AvatarManager.loadAvatar(mReceiverID, profile.profileFileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.bindAvatar(it)
                }, {
                    logError("onBindViewHolder", it)
                    view.bindAvatar(null)
                })
        mDisposable.add(subscribe)
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
        mUserInfoList = arguments.getSerializable(IntentKey.EXTRA_USER_INFO_LIST) as ArrayList<ProfileInfoEntity>?
                ?: run {
                    ChatFlowManager.queryChannelListByChannelType(mReceiverID, listOf(LTChannelType.SINGLE))
                            .doOnSubscribe {
                                logDebug("initBundle mUserInfoList ++")
                            }
                            .concatMapIterable {
                                it
                            }
                            .flatMap {
                                val userID = ChatFlowManager.getUserIDFromOneToOneChannel(mReceiverID, it.chID)
                                ProfileInfoManager.getProfileInfoByUserID(mReceiverID, userID)
                            }
                            .toList().toObservable()
                            .map {
                                ArrayList(it)
                            }
                            .blockingFirst()
                }
        arguments.remove(IntentKey.EXTRA_USER_INFO_LIST)
    }

    override fun checkCreateGroup() {
        if (mSelectedList.isEmpty()) {
            mView?.showSnackBar(R.string.picker_selected_list_is_empty)
            return
        }

        mView?.gotoCreateGroupPage(mSelectedList)
    }

    override fun operateCheck(position: Int, isChecked: Boolean) {
        val userInfoEntity = mUserInfoList[position]
        if (isChecked) {
            mSelectedList.add(userInfoEntity)
        } else {
            mSelectedList.remove(userInfoEntity)
        }
    }
}
