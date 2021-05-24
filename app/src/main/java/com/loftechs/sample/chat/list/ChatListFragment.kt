package com.loftechs.sample.chat.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.chat.chatroom.ChatFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.component.CustomLinearLayoutManager
import com.loftechs.sdk.im.channels.LTChannelResponse
import com.loftechs.sdk.im.channels.LTChannelType

class ChatListFragment : AbstractFragment(), ChatListContract.View {

    private var mPresenter: ChatListContract.Presenter<ChatListContract.View>? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatListAdapter: ChatListAdapter

    companion object {
        fun newInstance(): ChatListFragment {
            return ChatListFragment()
        }
    }

    override fun gotoChatPage(
            channelID: String,
            channelType: LTChannelType,
            subject: String,
            memberCount: Int,
            lastMsgTime: Long,
    ) {
        changeFragment(ChatFragment.newInstance, requireArguments().apply {
            putString(IntentKey.EXTRA_CHANNEL_ID, channelID)
            putSerializable(IntentKey.EXTRA_CHANNEL_TYPE, channelType)
            putString(IntentKey.EXTRA_CHANNEL_SUBJECT, subject)
            putInt(IntentKey.EXTRA_CHANNEL_MEMBER_COUNT, memberCount)
            putLong(IntentKey.EXTRA_CHANNEL_LAST_MESSAGE_TIME, lastMsgTime)
        })
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: ChatListPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
        mChatListAdapter = ChatListAdapter(mPresenter)
        mRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mChatListAdapter
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.chat_list_recycler_view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    override fun refreshChatList(items: List<LTChannelResponse>) {
        mChatListAdapter.submitList(items)
    }
}