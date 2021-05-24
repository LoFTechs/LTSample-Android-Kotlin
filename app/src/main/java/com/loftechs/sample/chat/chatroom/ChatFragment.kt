package com.loftechs.sample.chat.chatroom

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.call.list.CallState
import com.loftechs.sample.call.voice.VoiceCallFragment
import com.loftechs.sample.chat.message.CustomImageMessageViewHolder
import com.loftechs.sample.chat.message.CustomTextMessageViewHolder
import com.loftechs.sample.chat.message.SystemMessageViewHolder
import com.loftechs.sample.chat.settings.ChatSettingsFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.component.GlideApp
import com.loftechs.sample.extensions.*
import com.loftechs.sample.model.data.message.BaseMessage
import com.loftechs.sample.utils.*
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import java.io.File
import java.util.*


class ChatFragment : AbstractFragment(), ChatContract.View {

    private var mPresenter: ChatContract.Presenter<ChatContract.View>? = null

    private lateinit var mToolbar: Toolbar
    private lateinit var mMessageInput: MessageInput
    private lateinit var mMessageList: MessagesList
    private lateinit var mMessageListAdapter: MessagesListAdapter<BaseMessage>

    private val mContentChecker: MessageHolders.ContentChecker<BaseMessage> by lazy {
        MessageHolders.ContentChecker { _, _ ->
            true
        }
    }

    companion object {
        val newInstance
            get() = ChatFragment()
        private const val CUSTOM_CONTENT_TYPE_SYSTEM = 102
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: ChatPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.loadMessages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mMessageList = view.findViewById(R.id.chat_message_recycler_view)
        initToolbar(view)
        initMessageAdapter()
        initInputBar(view)
    }

    private fun initToolbar(view: View) {
        mToolbar = view.findViewById(R.id.chat_toolbar)
        mToolbar.setNavigationOnClickListener {
            finishChat()
        }
        displayTitle()
        mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.call -> {
                    changeFragment(VoiceCallFragment.newInstance(), requireArguments().apply {
                        putString(IntentKey.EXTRA_CALL_USER_ID, mPresenter?.getUserIDFromOneToOne())
                        putInt(IntentKey.EXTRA_CALL_STATE_TYPE, CallState.OUT.ordinal)
                    })
                    true
                }
                R.id.group_info -> {
                    gotoChatSettingPage()
                    true
                }
                R.id.clear_chat -> {
                    AlertDialog.Builder(requireContext())
                            .setMessage(R.string.chat_clear_chat_message)
                            .setPositiveButton(R.string.common_confirm) { dialogInterface, _ ->
                                mPresenter?.clearChatMessages()
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton(R.string.common_cancel, null)
                            .show()
                    true
                }
                else -> false
            }
        }
        mToolbar.setOnClickListener {
            gotoChatSettingPage()
        }
    }

    override fun displayTitle() {
        mToolbar.title = mPresenter?.subject
        mToolbar.subtitle = mPresenter?.subtitle
    }

    override fun showCallIconInToolbar(isVisible: Boolean) {
        mToolbar.menu.findItem(R.id.call).isVisible = isVisible
    }

    private fun gotoChatSettingPage() {
        changeFragment(ChatSettingsFragment.newInstance(), requireArguments())
    }

    private fun initMessageAdapter() {
        mMessageListAdapter = MessagesListAdapter(mPresenter?.receiverID, getCustomHolders()) { imageView, filePath, _ ->
            logThread("initView ++ ImageLoader filePath: $filePath")
            filePath?.let {
                GlideApp.with(imageView)
                        .load(filePath)
                        .into(imageView)
            }
        }
        mMessageListAdapter.setOnMessageClickListener {
            mPresenter?.messageClick(it)
        }
        mMessageListAdapter.setLoadMoreListener { page, totalItemsCount ->
            logDebug("initView ++ setLoadMoreListener: page: $page, count: $totalItemsCount")
            mPresenter?.loadMessages()
        }
        mMessageListAdapter.setOnMessageLongClickListener {
            val itemList = mPresenter?.getLongClickMenuItemList(it)
            AlertDialog.Builder(requireContext())
                    .setItems(itemList) { dialogInterface, position ->
                        when (position) {
                            0 -> {
                                showRecallMessageDialog(it)
                            }
                            1 -> {
                                showDeleteMessageDialog(it)
                            }
                            2 -> {
                                KeyboardUtil.copyToKeyboard(requireContext(), it.text)
                                showSnackBar(R.string.chat_text_copy_message)
                            }
                        }
                        dialogInterface.dismiss()
                    }
                    .show()
        }
        mMessageList.setAdapter(mMessageListAdapter)
    }

    private fun showRecallMessageDialog(message: BaseMessage) {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.chat_recall_message)
                .setPositiveButton(R.string.item_recall) { dialogInterface, _ ->
                    mPresenter?.recallMessage(message)
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }

    private fun showDeleteMessageDialog(message: BaseMessage) {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.chat_delete_message)
                .setPositiveButton(R.string.item_delete) { dialogInterface, _ ->
                    mPresenter?.deleteMessage(message)
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }

    private fun initInputBar(view: View) {
        mMessageInput = view.findViewById(R.id.chat_message_input)
        mMessageInput.setInputListener {
            if (it.isNotEmpty()) {
                mPresenter?.sendTextMessage(it.toString())
                return@setInputListener true
            }
            return@setInputListener false
        }
        mMessageInput.setAttachmentsListener {
            AlertDialog.Builder(requireContext())
                    .setItems(arrayOf(getString(R.string.item_image))) { dialogInterface, i ->
                        if (i == 0) {
                            pickImageFromExternal()
                        }
                        dialogInterface.dismiss()
                    }
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        logDebug("onActivityResult from ChatFragment + $requestCode, $data")
        if (requestCode == REQUEST_SELECT_IMAGE) {
            mPresenter?.sendImageMessage(data)
        }
    }

    override fun refreshSpecificMessage(message: BaseMessage) {
        mMessageListAdapter.update(message)
    }

    override fun deleteMessage(message: BaseMessage) {
        mMessageListAdapter.delete(message)
    }

    override fun refreshNewMessage(message: BaseMessage) {
        mMessageListAdapter.addToStart(message, true)
    }

    override fun clearMessages() {
        mMessageListAdapter.clear()
    }

    override fun refreshMessageList(messageList: ArrayList<BaseMessage>) {
        mMessageListAdapter.addToEnd(messageList, false)
    }

    override fun viewImage(file: File) {
        logDebug("viewImage ++ filePath: ${file.path}")
        viewPhotoInExternal(file.getFileProviderUri(requireContext()))
    }

    override fun finishChat() {
        activity?.onBackPressed()
    }

    private fun getCustomHolders(): MessageHolders {
        return MessageHolders()
                .setIncomingTextConfig(
                        CustomTextMessageViewHolder::class.java,
                        R.layout.custom_holder_text
                )
                .setIncomingImageConfig(
                        CustomImageMessageViewHolder::class.java,
                        R.layout.custom_holder_image
                )
                .registerContentType(
                        CUSTOM_CONTENT_TYPE_SYSTEM.toByte(),
                        SystemMessageViewHolder::class.java, R.layout.custom_holder_system,
                        SystemMessageViewHolder::class.java, R.layout.custom_holder_system,
                        mContentChecker
                )
    }
}