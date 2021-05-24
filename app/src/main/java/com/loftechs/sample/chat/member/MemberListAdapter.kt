package com.loftechs.sample.chat.member

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.loftechs.sample.R
import com.loftechs.sample.chat.member.MemberListAdapter.MemberListViewHolder
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.model.data.MemberEntity


class MemberListAdapter(
        private val mPresenter: MemberListContract.Presenter<MemberListContract.View>?,
) : ListAdapter<MemberEntity, MemberListViewHolder>(MemberListDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListViewHolder {
        return MemberListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_member, parent, false))
    }

    override fun onBindViewHolder(holder: MemberListViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class MemberListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, IItemView {
        private val avatarView: ImageView = itemView.findViewById(R.id.profile_avatar)
        private val displayView: TextView = itemView.findViewById(R.id.name_text_view)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)

        init {
            deleteButton.setOnClickListener { v: View -> onClick(v) }
        }

        override fun onClick(v: View) {
            mPresenter?.requestRemoveMember(getItem(absoluteAdapterPosition).userID, displayView.text.toString())
        }

        override fun bindAvatar(uri: Uri?) {
            avatarView.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        }

        override fun setDisplayName(text: String) {
            displayView.text = text
        }

        override fun enableDeleteButton(enable: Boolean) {
            if (enable) {
                deleteButton.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }

    interface IItemView {
        fun bindAvatar(uri: Uri?)
        fun setDisplayName(text: String)
        fun enableDeleteButton(enable: Boolean)
    }
}

class MemberListDiff : DiffUtil.ItemCallback<MemberEntity>() {
    override fun areItemsTheSame(oldItem: MemberEntity, newItem: MemberEntity): Boolean {
        return oldItem.userID == newItem.userID
    }

    override fun areContentsTheSame(oldItem: MemberEntity, newItem: MemberEntity): Boolean {
        return oldItem == newItem
    }
}