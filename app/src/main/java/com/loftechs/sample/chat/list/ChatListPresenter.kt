package com.loftechs.sample.chat.list

import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.ChatCloseEvent
import com.loftechs.sample.common.event.ChatEvent
import com.loftechs.sample.common.event.IncomingMessageEvent
import com.loftechs.sample.common.event.MarkReadEvent
import com.loftechs.sample.extensions.getShowMessage
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.extensions.logThread
import com.loftechs.sample.main.MainActivity
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.UserManager
import com.loftechs.sample.utils.DateFormatUtil
import com.loftechs.sdk.extension.rx.getUsers
import com.loftechs.sdk.im.channels.LTChannelResponse
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.im.message.LTMessageType
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.user.LTUserManager
import com.loftechs.sdk.utils.LTLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.ConcurrentHashMap

class ChatListPresenter : ChatListContract.Presenter<ChatListContract.View> {

    private var mView: ChatListContract.View? = null

    private lateinit var mReceiverID: String
    override val receiverID: String
        get() = mReceiverID

    // key chatID
    private val mChatMap: ConcurrentHashMap<String, LTChannelResponse> by lazy {
        ConcurrentHashMap()
    }

    private val mChannelTypeList: ArrayList<LTChannelType> by lazy {
        arrayListOf(LTChannelType.SINGLE, LTChannelType.GROUP)
    }

    private val mChatList: ArrayList<LTChannelResponse> by lazy {
        arrayListOf()
    }

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun create() {
        EventBus.getDefault().register(this)
    }

    override fun resume() {
        if (mChatMap.isEmpty()) {
            loadChatList()
        }
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
        EventBus.getDefault().unregister(this)
    }

    override fun bindView(view: ChatListContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun loadChatList() {
        val subscribe = ChatFlowManager.queryChannelListByChannelType(receiverID, mChannelTypeList)
                .flatMap {
                    logDebug("loadChatList size: ${it.size}")
                    updateChatListToMapAndSort(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refreshUI(it)
                }, {
                    logError("loadChatList", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)

    }

    private fun refreshUI(chatList: List<LTChannelResponse>) {
        logDebug("refreshUI size: ${chatList.size}")
        mChatList.clear()
        mChatList.addAll(chatList)
        mView?.refreshChatList(ArrayList(mChatList))
    }

    override fun onBindViewHolder(view: ChatListAdapter.IItemView, position: Int) {
        val channel = mChatList[position]
        val defaultDrawable: Int
        val profileObservable = if (channel.chType == LTChannelType.SINGLE) {
            val userID = ChatFlowManager.getUserIDFromOneToOneChannel(receiverID, channel.chID)
            defaultDrawable = R.drawable.ic_profile
            ProfileInfoManager.getProfileInfoByUserID(receiverID, userID)
        } else {
            defaultDrawable = R.drawable.ic_group_profile
            ProfileInfoManager.getProfileInfoByChatID(receiverID, channel.chID)
        }
        val subscribe = profileObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("id : ${it.id} name : ${it.displayName}")
                    view.setTitleText(it.displayName)
                    bindAvatar(view, it.profileFileInfo, defaultDrawable)
                }, {
                    logError("onBindViewHolder", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
        bindLastMessage(channel, view)
        view.setMessageTimeText(DateFormatUtil.getStringFormat(channel.lastMsgTime, "yyyy/MM/dd HH:mm:ss"))
        var unreadCount = ""
        if (channel.unreadCount > 0) {
            unreadCount = channel.unreadCount.toString()
        }
        view.enableMute(channel.isMute)
        view.setNewMessageCountText(unreadCount)
    }

    private fun bindLastMessage(channel: LTChannelResponse, view: ChatListAdapter.IItemView) {
        when (channel.lastMsgType) {
            LTMessageType.TYPE_IMAGE,
            LTMessageType.TYPE_RECALL_MESSAGE,
            LTMessageType.TYPE_ANSWER_INVITATION,
            LTMessageType.TYPE_CREATE_CHANNEL,
            LTMessageType.TYPE_INVITE_MEMBER,
            LTMessageType.TYPE_KICK_MEMBERS,
            LTMessageType.TYPE_LEAVE_CHANNEL,
            LTMessageType.TYPE_DISMISS_CHANNEL,
            LTMessageType.TYPE_SET_CHANNEL_PROFILE,
            -> {
                view.setLastMsgText(channel.lastMsgType.getShowMessage())
            }
            else -> {
                view.setLastMsgText(channel.lastMsgContent)
            }
        }
    }

    private fun bindAvatar(view: ChatListAdapter.IItemView, fileInfo: LTFileInfo?, defaultDrawable: Int) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.bindAvatar(it, defaultDrawable)
                }, {
                    logError("bindAvatar", it)
                    view.bindAvatar(null, defaultDrawable)
                })
        mDisposable.add(subscribe)
    }

    override fun onItemClick(response: LTChannelResponse, subject: String) {
        mView?.gotoChatPage(response.chID, response.chType, subject, response.memberCount, response.lastMsgTime)
    }

    private fun sortChatList(): List<LTChannelResponse> {
        val chatList = mChatMap.values
        return chatList.sortedByDescending { chat ->
            chat.lastMsgTime
        }
    }

    private fun loadChatByID(id: String) {
        val subscribe = ChatFlowManager.queryChannelByID(receiverID, id)
                .flatMap {
                    updateChatListToMapAndSort(arrayListOf(it))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("loadChatByID onNext: $it")
                    refreshUI(it)
                }, {
                    logError("loadChatByID", it)
                })
        mDisposable.add(subscribe)
    }

    private fun updateChatListToMapAndSort(chatList: List<LTChannelResponse>): Observable<List<LTChannelResponse>> {
        return Observable.fromIterable(chatList)
                .toMap(LTChannelResponse::getChID)
                .toObservable()
                .map {
                    mChatMap.putAll(it)
                    sortChatList()
                }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: IncomingMessageEvent) {
        logThread("IncomingMessageEvent++ $event")
        if (event.receiverID != mReceiverID) {
            return
        }
        if (event.channelID.isNotEmpty()) {
            loadChatByID(event.channelID)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: ChatEvent) {
        logDebug("ChatEvent  $event")
        if (event.receiverID != mReceiverID) {
            return
        }
        if (event.chID.isEmpty()) {
            logDebug("chatEvent requestAllChat.")
            loadChatList()
        } else {
            logDebug("chatEvent queryChannelByID : ${event.chID}")
            loadChatByID(event.chID)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: ChatCloseEvent) {
        logDebug("ChatCloseEvent  $event")
        if (event.receiverID != mReceiverID) {
            return
        }
        if (event.chID.isNotEmpty()) {
            mChatMap.remove(event.chID)
            refreshUI(sortChatList())
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: MarkReadEvent) {
        logDebug("MarkReadEvent  $event")
        if (event.receiverID != mReceiverID) {
            return
        }
        loadChatByID(event.channelID)
    }
}