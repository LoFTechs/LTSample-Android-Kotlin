package com.loftechs.sample.chat.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.loftechs.sample.R
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sdk.im.channels.LTChannelResponse

class ChatListAdapter(
        private val mPresenter: ChatListContract.Presenter<ChatListContract.View>?,
) : ListAdapter<LTChannelResponse, ChatListAdapter.ChatListViewHolder>(ChatListDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_chat_list, parent, false))
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IItemView {
        private val titleView: TextView = itemView.findViewById(R.id.chat_list_title)
        private val lastMessageView: TextView = itemView.findViewById(R.id.chat_list_content)
        private val timeView: TextView = itemView.findViewById(R.id.chat_list_time)
        private val newMessageCountView: TextView = itemView.findViewById(R.id.new_message_count)
        private val avatarView: ImageView = itemView.findViewById(R.id.profile_image)
        private val muteImageView: ImageView = itemView.findViewById(R.id.chat_mute)

        init {
            itemView.setOnClickListener {
                mPresenter?.onItemClick(getItem(absoluteAdapterPosition), titleView.text.toString())
            }
        }

        override fun bindAvatar(uri: Uri?, defaultDrawable: Int) {
            avatarView.loadImageWithGlide(defaultDrawable, uri, true)
        }

        override fun setTitleText(text: String) {
            titleView.text = text
        }

        override fun setLastMsgText(text: String) {
            lastMessageView.text = text
        }

        override fun setNewMessageCountText(text: String) {
            if (text.isEmpty()) {
                newMessageCountView.visibility = View.GONE
                return
            }
            newMessageCountView.visibility = View.VISIBLE
            newMessageCountView.text = text
        }

        override fun setMessageTimeText(text: String) {
            timeView.text = text
        }

        override fun enableMute(enable: Boolean) {
            if (enable) {
                muteImageView.visibility = View.VISIBLE
            } else {
                muteImageView.visibility = View.GONE
            }
        }
    }

    interface IItemView {
        fun bindAvatar(uri: Uri?, defaultDrawable: Int)
        fun setTitleText(text: String)
        fun setLastMsgText(text: String)
        fun setNewMessageCountText(text: String)
        fun setMessageTimeText(text: String)
        fun enableMute(enable: Boolean)
    }
}

class ChatListDiff : DiffUtil.ItemCallback<LTChannelResponse>() {
    override fun areItemsTheSame(oldItem: LTChannelResponse, newItem: LTChannelResponse): Boolean {
        return oldItem.chID == newItem.chID
    }

    override fun areContentsTheSame(oldItem: LTChannelResponse, newItem: LTChannelResponse): Boolean {
        return oldItem == newItem
    }
}