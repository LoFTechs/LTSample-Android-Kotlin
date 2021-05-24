package com.loftechs.sample.chat.chatroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_ID
import com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_LAST_MESSAGE_TIME
import com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_MEMBER_COUNT
import com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_SUBJECT
import com.loftechs.sample.common.IntentKey.EXTRA_CHANNEL_TYPE
import com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID
import com.loftechs.sample.common.event.ChatCloseEvent
import com.loftechs.sample.common.event.ChatEvent
import com.loftechs.sample.common.event.IncomingMessageEvent
import com.loftechs.sample.common.event.MarkReadEvent
import com.loftechs.sample.extensions.*
import com.loftechs.sample.model.MessageFlowManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.RemoteFileManager
import com.loftechs.sample.model.api.isDone
import com.loftechs.sample.model.data.message.*
import com.loftechs.sample.utils.*
import com.loftechs.sdk.im.channels.*
import com.loftechs.sdk.im.message.*
import com.loftechs.sdk.im.message.LTMessageType.*
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.storage.LTStorageManager
import com.loftechs.sdk.storage.LTStorageResult
import com.loftechs.sdk.utils.ExtUtil
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.properties.Delegates

class ChatPresenter : ChatContract.Presenter<ChatContract.View> {

    private var mView: ChatContract.View? = null

    companion object {
        private val TAG = ChatPresenter::class.java.simpleName
    }

    override lateinit var receiverID: String
    private lateinit var mChannelID: String
    private lateinit var mChannelType: LTChannelType
    override lateinit var subject: String
    override val subtitle: String
        get() {
            return when (mChannelType) {
                LTChannelType.SINGLE -> {
                    SampleApp.context.resources.getString(R.string.chat_subtitle_single)
                }
                LTChannelType.GROUP -> {
                    return if (mMemberCount > 1) {
                        SampleApp.context.resources.getString(R.string.chat_subtitle_members, mMemberCount)
                    } else {
                        SampleApp.context.resources.getString(R.string.chat_subtitle_member_only_one)
                    }
                }
                else -> {
                    SampleApp.context.resources.getString(R.string.chat_subtitle_single)
                }
            }
        }

    private var mMemberCount by Delegates.notNull<Int>()
    private var mLastMessageTime by Delegates.notNull<Long>()

    private val mMessageList: ArrayList<BaseMessage> by lazy {
        ArrayList()
    }

    private var mRequestTime: Long? = null

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
        receiverID = arguments.getString(EXTRA_RECEIVER_ID, "")
        mChannelID = arguments.getString(EXTRA_CHANNEL_ID, "")
        mChannelType = arguments.getSerializable(EXTRA_CHANNEL_TYPE) as LTChannelType
        subject = arguments.getString(EXTRA_CHANNEL_SUBJECT, "")
        mMemberCount = arguments.getInt(EXTRA_CHANNEL_MEMBER_COUNT, 0)
        mLastMessageTime = arguments.getLong(EXTRA_CHANNEL_LAST_MESSAGE_TIME, 0L)
        arguments.remove(EXTRA_CHANNEL_MEMBER_COUNT)
        arguments.remove(EXTRA_CHANNEL_LAST_MESSAGE_TIME)
    }

    override fun create() {
        EventBus.getDefault().register(this)
    }

    override fun resume() {
        mView?.showCallIconInToolbar(mChannelType == LTChannelType.SINGLE)
        if (mLastMessageTime > 0L) {
            sendMarkRead(mLastMessageTime)
        }
    }

    override fun pause() {
    }

    override fun destroy() {
        EventBus.getDefault().unregister(this)
        mDisposable.clear()
    }

    override fun bindView(view: ChatContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
        mRequestTime = null
        mMessageList.clear()
    }

    private fun sendMarkRead(markReadTime: Long) {
        val subscribe = MessageFlowManager.markRead(receiverID, mChannelID, markReadTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("sendMarkRead onNext ++ $it")
                    EventBus.getDefault().post(MarkReadEvent(receiverID, mChannelID))
                }, {
                    logError("sendMarkRead", it)
                })
        mDisposable.add(subscribe)
    }

    override fun loadMessages() {
        loadMessagesByTime(mRequestTime ?: System.currentTimeMillis())
    }

    private fun loadMessagesByTime(time: Long) {
        logDebug("loadMessagesByTime ++ $time")
        val subscribe = MessageFlowManager.sendQueryMessage(receiverID, mChannelID, time, -20)
                .filter {
                    it.messages.isNotEmpty()
                }
                .doOnNext {
                    mRequestTime = it.messages[it.messages.size - 1].sendTime
                }
                .flatMapSingle {
                    convertToIMessage(it.messages)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("loadMessagesByTime onNext: $it")
                    mMessageList.addAll(it)
                    mView?.refreshMessageList(it)
                }, {
                    logError("loadMessagesByTime", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    private fun convertToIMessage(messages: MutableList<LTMessageResponse>): Single<ArrayList<BaseMessage>> {
        return Observable.fromIterable(messages)
                .filter {
                    it !is LTDeleteChannelMessageResponse
                            && it !is LTRecallMessagesResponse
                            && it !is LTDeleteMessagesResponse
                            && it !is LTChannelPreferenceResponse
                }
                .concatMap { messageResponse ->
                    if (messageResponse is LTSendMessageResponse) {
                        val message = messageResponse.message
                        if (message is LTThumbnailFileMessage
                                && (message is LTImageMessage || message is LTVideoMessage)) {
                            val fileInfo = message.thumbnailFileInfo
                            val thumbnailFile = FileUtil.getThumbnailFile(fileInfo.filename, true)
                            logThread("convertToIMessage ++ ${thumbnailFile.exists()} / ${thumbnailFile.length()}")
                            if (thumbnailFile.length() == 0L) {
                                return@concatMap downloadFile(fileInfo, thumbnailFile)
                                        .map {
                                            messageResponse
                                        }
                            }
                        }
                    }
                    Observable.just(messageResponse)
                }
                .map { messageResponse ->
                    val sender = User(messageResponse.senderID,
                            messageResponse.senderNickname ?: "")

                    when (messageResponse) {
                        is LTSendMessageResponse -> {
                            if (messageResponse.recallStatus != null) {
                                return@map SystemMessage(messageResponse.msgID, TYPE_RECALL_MESSAGE.getShowMessage(), sender, Date(messageResponse.sendTime))
                            }

                            when (val message = messageResponse.message) {
                                is LTImageMessage -> {
                                    val thumbnailFile = FileUtil.getThumbnailFile(message.thumbnailFileInfo.filename, false)
                                    ImageMessage(messageResponse.msgID, messageResponse.msgContent, sender,
                                            Date(messageResponse.sendTime), thumbnailFile.path, message.fileInfo)
                                }
                                else -> {
                                    TextMessage(messageResponse.msgID, messageResponse.msgContent, sender, Date(messageResponse.sendTime))
                                }
                            }
                        }
                        is LTRecallMessagesResponse,
                        is LTJoinChannelResponse,
                        is LTCreateChannelResponse,
                        is LTInviteMemberResponse,
                        is LTKickMemberResponse,
                        is LTLeaveChannelResponse,
                        is LTDismissChannelResponse,
                        is LTChannelProfileResponse,
                        -> {
                            SystemMessage(messageResponse.msgID, messageResponse.msgType.getShowMessage(), sender, Date(messageResponse.sendTime))
                        }
                        else -> {
                            TextMessage(messageResponse.msgID, messageResponse.msgType.getShowMessage(), sender, Date(messageResponse.sendTime))
                        }
                    }
                }
                .collect({
                    ArrayList<BaseMessage>()
                }, { list, item ->
                    list.add(item)
                })
    }

    private fun downloadFile(fileInfo: LTFileInfo, file: File): Observable<LTStorageResult> {
        return RemoteFileManager.downloadFile(receiverID, fileInfo, file)
                .filter {
                    it.isDone()
                }
                .doOnNext {
                    logDebug("downloadFile $it")
                }
                .doOnError {
                    logError("downloadFile", it)
                }
    }

    override fun sendTextMessage(textToSend: String) {
        val subscribe = MessageFlowManager.sendTextMessage(receiverID, mChannelID, mChannelType, textToSend)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.tag(TAG).d("sendTextMessage sendText: $textToSend, onNext: $it")
                    EventBus.getDefault().post(IncomingMessageEvent(receiverID, mChannelID, it))
                }, {
                    Timber.tag(TAG).e("sendTextMessage onError: $it")
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    override fun sendImageMessage(intent: Intent?) {
        intent?.data?.let { sourceUri ->
            val transID = Utils.createTransId()
            val originalFile = convertContentUriToFile(transID, sourceUri, true)
            val originalUri = Uri.fromFile(originalFile)

            logDebug("sendImageMessage ++ file: ${originalFile.path}")

            val subscribe = MessageFlowManager.sendImageMessage(receiverID, mChannelID,
                    mChannelType, originalUri, originalUri, originalFile.name)
                    .doOnNext {
                        if (it.fileMessageStatus == LTFileMessageStatus.STATUS_FILE) {
                            for (result in it.fileTransferResults) {
                                val fileType = result.fileType
                                val status = result.status
                                if (fileType == LTFileType.TYPE_FILE
                                        && status == LTStorageManager.StorageStatus.UPLOAD_LOADING) {
                                    val percentage = (result.loadingBytes.toDouble() / result.totalLength.toDouble() * 100).toInt()
                                    logDebug("sendImageMessage transfer: $percentage%")
                                }
                            }
                        }
                    }
                    .filter {
                        it.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        mView?.showProgressDialog()
                    }
                    .subscribe({
                        mView?.dismissProgressDialog()
                        EventBus.getDefault().post(IncomingMessageEvent(receiverID, mChannelID, it))
                    }, {
                        logError("sendImageMessage", it)
                        it.printStackTrace()
                        mView?.dismissProgressDialog()
                    })
            mDisposable.add(subscribe)
        }
    }

    private fun convertContentUriToFile(transID: String, uri: Uri, isImage: Boolean): File {
        val sourceFileName = uri.getFileName()
        val filename = "${transID}.${ExtUtil.getExtension(sourceFileName)}"
        val originalFile = FileUtil.getOriginalFile(filename, true)

        if (isImage) {
            uri.writeToImage(originalFile)
        } else {
            uri.writeToFile(originalFile)
        }

        return originalFile
    }

    override fun messageClick(message: BaseMessage) {
        if (message is ImageMessage) {
            message.fileInfo?.let { fileInfo ->
                val file = FileUtil.getOriginalFile(fileInfo.filename, true)
                logDebug("messageClick downloadOriginalFile ++ path: ${file.path} / ${file.length()}")

                if (file.length() == 0L) {
                    downloadFile(fileInfo, file)
                            .subscribe({
                                logDebug("messageClick downloadOriginalFile ++ $it, path: ${file.path}")
                                mView?.viewImage(file)
                            }, {
                                logError("messageClick downloadOriginalFile", it)
                                it.printStackTrace()
                            })
                } else {
                    mView?.viewImage(file)
                }
            }
        }
    }

    override fun recallMessage(message: BaseMessage) {
        val position = mMessageList.indexOf(message)
        val subscribe = MessageFlowManager.recallMessage(receiverID, message.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("recallMessage ++ onNext: ${it.recallMsgID}")
                    mMessageList[position] = SystemMessage(message.id,
                            TYPE_RECALL_MESSAGE.getShowMessage(), message.user, message.createdAt)
                    mView?.refreshSpecificMessage(mMessageList[position])
                }, {
                    logError("recallMessage", it)
                    it.printStackTrace()
                    mView?.showSnackBar(R.string.chat_recall_error)
                })
        mDisposable.add(subscribe)
    }

    override fun deleteMessage(message: BaseMessage) {
        val position = mMessageList.indexOf(message)
        val subscribe = MessageFlowManager.deleteMessage(receiverID, message.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("deleteMessage ++ onNext: ${it.deleteMsgID}")
                    mMessageList.removeAt(position)
                    mView?.deleteMessage(message)
                }, {
                    logError("deleteMessage", it)
                    it.printStackTrace()
                    mView?.showSnackBar(R.string.chat_delete_error)
                })
        mDisposable.add(subscribe)
    }

    override fun clearChatMessages() {
        val subscribe = ChatFlowManager.deleteChannelMessages(receiverID, mChannelID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("clearChatMessages ++ onNext: ${it.chID}")
                    mMessageList.clear()
                    mRequestTime = null
                    mView?.clearMessages()
                }, {
                    logError("clearChatMessages", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    override fun getLongClickMenuItemList(message: BaseMessage): Array<String> {
        return if (message is TextMessage) {
            arrayOf(
                    SampleApp.context.resources.getString(R.string.item_recall),
                    SampleApp.context.resources.getString(R.string.item_delete),
                    SampleApp.context.resources.getString(R.string.item_copy)
            )
        } else {
            arrayOf(
                    SampleApp.context.resources.getString(R.string.item_recall),
                    SampleApp.context.resources.getString(R.string.item_delete)
            )
        }
    }

    override fun getUserIDFromOneToOne(): String {
        return if (mChannelType == LTChannelType.SINGLE) {
            ChatFlowManager.getUserIDFromOneToOneChannel(receiverID, mChannelID)
        } else {
            ""
        }
    }

    private fun refreshMessage(message: BaseMessage) {
        mMessageList.add(0, message)
        mView?.refreshNewMessage(message)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: IncomingMessageEvent) {
        logThread("IncomingMessageEvent++ $event")

        if (event.channelID != mChannelID) {
            return
        }

        val response = event.response
        val subscribe = convertToIMessage(mutableListOf(response))
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refreshMessage(it[0])
                    sendMarkRead(response.sendTime)
                }, {
                    logError("IncomingMessageEvent", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ChatEvent) {
        logDebug("ChatEvent  $event")
        if (event.receiverID != receiverID) {
            return
        }
        if (event.chID == mChannelID) {
            if (event.subject.isNotEmpty()) {
                subject = event.subject
            }
            if (event.memberCount > 0) {
                mMemberCount = event.memberCount
            }
            mView?.displayTitle()
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEventBackgroundThread(event: ChatCloseEvent) {
        logDebug("ChatCloseEvent  $event")
        if (event.receiverID != receiverID) {
            return
        }
        if (event.chID.isNotEmpty() && event.chID == mChannelID) {
            mView?.finishChat()
        }
    }
}
