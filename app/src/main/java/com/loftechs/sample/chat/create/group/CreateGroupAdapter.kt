package com.loftechs.sample.chat.create.group

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.loftechs.sample.R
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.model.data.ProfileInfoEntity

class CreateGroupAdapter(
        private val mPresenter: CreateGroupContract.Presenter<CreateGroupContract.View>?,
) : ListAdapter<ProfileInfoEntity, CreateGroupAdapter.SelectedItemViewHolder>(CreateGroupDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder {
        return SelectedItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: SelectedItemViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class SelectedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ISelectedItemView {
        private val avatarView: ImageView = itemView.findViewById(R.id.item_contact_avatar)
        private val title: MaterialTextView = itemView.findViewById(R.id.item_contact_title)

        override fun bindAvatar(uri: Uri?) {
            avatarView.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        }

        override fun setTitleText(text: String) {
            title.text = text
        }
    }

    interface ISelectedItemView {
        fun bindAvatar(uri: Uri?)
        fun setTitleText(text: String)
    }
}

class CreateGroupDiff : DiffUtil.ItemCallback<ProfileInfoEntity>() {
    override fun areItemsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem == newItem
    }
}


