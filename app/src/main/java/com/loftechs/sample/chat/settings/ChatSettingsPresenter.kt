package com.loftechs.sample.chat.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.switchmaterial.SwitchMaterial
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.ChatCloseEvent
import com.loftechs.sample.common.event.ChatEvent
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.extensions.remove
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.ChatSettingsManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sample.utils.FileUtil
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.storage.LTFileInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatSettingsPresenter : ChatSettingsContract.Presenter<ChatSettingsContract.View> {

    private var mView: ChatSettingsContract.View? = null

    private lateinit var mReceiverID: String
    private lateinit var mChID: String
    private lateinit var mSubject: String
    private lateinit var mChannelType: LTChannelType
    private lateinit var mOrgMuteState: ChatSettingsData
    private lateinit var mProfileEntity: ProfileInfoEntity
    private var mDefaultDrawable: Int = R.drawable.ic_profile
    private var mItems: ArrayList<ChatSettingsData> = arrayListOf()
    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun create() {
    }

    override fun resume() {
        initData()
        EventBus.getDefault().register(this)
    }

    private fun initData() {
        requestData(mReceiverID, mChID)
        requestProfile()
    }

    override fun pause() {
        mDisposable.clear()
        EventBus.getDefault().unregister(this)
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: ChatSettingsContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
        mChID = arguments.getString(IntentKey.EXTRA_CHANNEL_ID, "")
        mChannelType = arguments.getSerializable(IntentKey.EXTRA_CHANNEL_TYPE) as LTChannelType
        mSubject = arguments.getString(IntentKey.EXTRA_CHANNEL_SUBJECT, "")
    }

    override fun requestData(userID: String, chID: String) {
        val subscribe = ChatSettingsManager.getChannelInfo(mReceiverID, mChID)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .doOnNext {
                    mView?.dismissProgressDialog()
                    mItems.clear()
                    // mute chat
                    mOrgMuteState = ChatSettingsData(ItemType.VIEW_TYPE_SWITCH, SampleApp.context.getString(R.string.chat_setting_mute), it.isMute)
                    mItems.add(mOrgMuteState)
                    // member list
                    if (canEditAvatarOrSubject()) {
                        mItems.add(ChatSettingsData(ItemType.VIEW_TYPE_TEXT, SampleApp.context.getString(R.string.chat_setting_member_list), false))
                    }
                    // dismiss chat
                    mItems.add(ChatSettingsData(ItemType.VIEW_TYPE_TEXT_RED, SampleApp.context.getString(R.string.chat_setting_dismiss), false))
                    // leave chat
                    mItems.add(ChatSettingsData(ItemType.VIEW_TYPE_TEXT_RED, SampleApp.context.getString(R.string.chat_setting_leave), false))
                    mView?.refreshList(mItems)
                }
                .doOnError {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.chat_setting_get_channel_info_error)
                }
                .subscribe()
        mDisposable.add(subscribe)
    }

    override fun updateChannelSubject(subject: String) {
        val subscribe = ChatSettingsManager.setChannelSubject(mReceiverID, mChID, subject)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    mView?.dismissProgressDialog()
                    mView?.setSubject(subject)
                    ProfileInfoManager.cleanProfileInfoByID(mChID)
                    val chatEvent = ChatEvent(mReceiverID, it.chID)
                    chatEvent.subject = subject
                    EventBus.getDefault().post(chatEvent)
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.chat_setting_update_subject_error)
                })
        mDisposable.add(subscribe)
    }

    private fun updateNotifyMute(mute: Boolean) {
        val subscribe = ChatSettingsManager.setChannelMute(mReceiverID, mChID, mute)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    EventBus.getDefault().post(ChatEvent(mReceiverID, it.chID))
                    mOrgMuteState.isOpen = !mOrgMuteState.isOpen
                    mItems[0] = mOrgMuteState
                }, {
                    mView?.showSnackBar(R.string.chat_setting_set_mute_error)
                    mView?.refreshList(mItems)
                })
        mDisposable.add(subscribe)
    }

    private fun executeAction(action: String) {
        when (action) {
            mView?.getStringValue(R.string.chat_setting_member_list) -> mView?.gotoMemberList()
            mView?.getStringValue(R.string.chat_setting_dismiss) -> dismissChannel()
            mView?.getStringValue(R.string.chat_setting_leave) -> leaveChannel()
        }
    }

    private fun dismissChannel() {
        val subscribe = ChatSettingsManager.dismissChannel(mReceiverID, mChID)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    FileUtil.getProfileFile("$mChID.jpg", false).remove()
                    ProfileInfoManager.cleanProfileInfoByID(it.chID)
                    EventBus.getDefault().post(ChatCloseEvent(mReceiverID, it.chID))
                    mView?.dismissProgressDialog()
                    mView?.finishChat()
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.chat_setting_dismiss_channel_error)
                })
        mDisposable.add(subscribe)
    }

    private fun leaveChannel() {
        val subscribe = ChatSettingsManager.leaveChannel(mReceiverID, mChID)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    FileUtil.getProfileFile("$mChID.jpg", false).remove()
                    ProfileInfoManager.cleanProfileInfoByID(it.chID)
                    EventBus.getDefault().post(ChatCloseEvent(mReceiverID, it.chID))
                    mView?.dismissProgressDialog()
                    mView?.finishChat()
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.chat_setting_leave_channel_error)
                })
        mDisposable.add(subscribe)
    }

    private fun requestProfile() {
        val displayObservable = if (mChannelType == LTChannelType.SINGLE) {
            val userID = ChatFlowManager.getUserIDFromOneToOneChannel(mReceiverID, mChID)
            ProfileInfoManager.getProfileInfoByUserID(mReceiverID, userID)
        } else {
            mDefaultDrawable = R.drawable.ic_group_profile
            ProfileInfoManager.getProfileInfoByChatID(mReceiverID, mChID)
        }
        val subscribe = displayObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mProfileEntity = it
                    mSubject = it.displayName
                    mView?.setSubject(mSubject)
                    bindAvatar(it.profileFileInfo)
                    logDebug("requestProfile : $it")
                }, {
                    logError("requestProfile", it)
                })
        mDisposable.add(subscribe)
    }

    private fun bindAvatar(fileInfo: LTFileInfo?) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.loadAvatar(it, mDefaultDrawable)
                }, {
                    logError("bindAvatar", it)
                    mView?.loadAvatar(null, mDefaultDrawable)
                })
        mDisposable.add(subscribe)
    }

    override fun deleteAvatar() {
        val subscribe = ChatFlowManager.deleteChannelAvatar(mReceiverID, mChID)
                .doOnNext {
                    FileUtil.getProfileFile("$mChID.jpg", false).remove()
                    ProfileInfoManager.cleanProfileInfoByID(mChID)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("deleteAvatar success.")
                    EventBus.getDefault().post(ChatEvent(mReceiverID, it.chID))
                    mView?.loadAvatar(null, mDefaultDrawable)
                }, {
                    logError("deleteAvatar", it)
                })
        mDisposable.add(subscribe)
    }

    override fun editAvatar() {
        if (mProfileEntity.profileFileInfo == null) {
            mView?.pickImage()
        } else {
            mView?.showEditAvatarDialog()
        }
    }

    override fun setProfileImage(intent: Intent?) {
        intent?.data?.let { uri ->
            logDebug("setProfileImage originalUri : $uri")
            uploadAvatar(uri)
        }
    }

    private fun uploadAvatar(uri: Uri) {
        val subscribe = AvatarManager.uploadChatAvatar(mReceiverID, mChID, uri)
                .doOnNext {
                    ProfileInfoManager.cleanProfileInfoByID(mChID)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("uploadAvatar success.")
                    EventBus.getDefault().post(ChatEvent(mReceiverID, it.chID))
                    mView?.loadAvatar(uri, mDefaultDrawable)
                }, {
                    logError("uploadAvatar", it)
                    mView?.showSnackBar(R.string.chat_setting_set_channel_profile_error)
                })
        mDisposable.add(subscribe)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ChatEvent) {
        logDebug("ChatEvent  $event")
        if (event.receiverID != mReceiverID || event.chID != mChID) {
            return
        }
        initData()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: ChatCloseEvent) {
        logDebug("ChatCloseEvent  $event")
        if (event.receiverID != mReceiverID || event.chID != mChID) {
            return
        }
        mView?.finishChat()
    }

    override fun onBindViewHolder(view: ChatSettingsAdapter.IItemView, position: Int) {
        val chatSettingsData = mItems[position]
        view.setTitleText(chatSettingsData.title)
        if (chatSettingsData.itemType == ItemType.VIEW_TYPE_SWITCH) {
            view.switchViewVisibility(true)
            view.switchViewIsChecked(chatSettingsData.isOpen)
        } else {
            view.switchViewVisibility(false)
        }

        if (chatSettingsData.itemType == ItemType.VIEW_TYPE_TEXT_RED) {
            view.setTextColor(R.color.color_red)
            if (chatSettingsData.title == SampleApp.context.getString(R.string.chat_setting_leave)) {
                view.setImageResource(R.drawable.ic_exit_group)
            }
        } else {
            view.setTextColor(R.color.chat_setting_text_color)
        }
    }

    override fun onItemClick(view: ChatSettingsAdapter.IItemView, data: ChatSettingsData) {
        if (view is SwitchMaterial) {
            updateNotifyMute(!data.isOpen)
            return
        }
        if (data.itemType == ItemType.VIEW_TYPE_SWITCH) {
            view.switchViewIsChecked(!data.isOpen)
            updateNotifyMute(!data.isOpen)
        } else {
            executeAction(data.title)
        }
    }

    override fun canEditAvatarOrSubject(): Boolean {
        return mChannelType != LTChannelType.SINGLE
    }
}