package com.loftechs.sample.chat.create.group

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.ChatEvent
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.message.LTMemberModel
import com.loftechs.sdk.storage.LTFileInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class CreateGroupPresenter : CreateGroupContract.Presenter<CreateGroupContract.View> {

    private var mView: CreateGroupContract.View? = null

    private lateinit var mReceiverID: String
    private lateinit var mSelectedList: ArrayList<ProfileInfoEntity>
    private var mChatAvatarUri: Uri? = null

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun create() {
    }

    override fun resume() {
        mView?.refreshList(ArrayList(mSelectedList))
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: CreateGroupContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun onBindViewHolder(view: CreateGroupAdapter.ISelectedItemView, position: Int) {
        val profileInfoEntity = mSelectedList[position]
        view.setTitleText(profileInfoEntity.displayName)
        bindAvatar(view, profileInfoEntity.profileFileInfo)
    }

    private fun bindAvatar(view: CreateGroupAdapter.ISelectedItemView, fileInfo: LTFileInfo?) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("bindAvatar $it")
                    view.bindAvatar(it)
                }, {
                    logError("bindAvatar", it)
                    view.bindAvatar(null)
                })
        mDisposable.add(subscribe)
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
        mSelectedList = arguments.getSerializable(IntentKey.EXTRA_SELECTED_LIST) as ArrayList<ProfileInfoEntity>
        arguments.remove(IntentKey.EXTRA_SELECTED_LIST)
    }

    override fun createGroup(subject: String) {
        if (subject.isEmpty()) {
            mView?.showSnackBar(R.string.create_group_set_subject_empty)
            return
        }

        val subscribe = ChatFlowManager.createGroupChannel(mReceiverID, subject, getMembers())
                .doOnNext {
                    uploadAvatar(it.chID)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .doOnError {
                    mView?.dismissProgressDialog()
                }
                .subscribe({
                    logDebug("createGroup ++ $it")
                    mView?.dismissProgressDialog()
                    mView?.gotoChatPage(it.chID, it.chType, subject, it.members.size)
                    EventBus.getDefault().post(ChatEvent(mReceiverID, it.chID))
                }, {
                    logError("createGroup", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    private fun getMembers(): Set<LTMemberModel> {
        val set = mutableSetOf<LTMemberModel>()
        for ((userID) in mSelectedList) {
            set.add(LTMemberModel(userID))
        }
        return set
    }

    override fun setProfileImage(intent: Intent?) {
        intent?.data?.let { uri ->
            logDebug("setProfileImage uri : $uri")
            mChatAvatarUri = uri
            mView?.loadAvatar(uri)
        }
    }

    private fun uploadAvatar(id: String) {
        mChatAvatarUri?.let { uri ->
            AvatarManager.uploadChatAvatar(mReceiverID, id, uri)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({
                        logDebug("uploadAvatar success.")
                    }, {
                        logError("uploadAvatar", it)
                    })
        }
    }
}
