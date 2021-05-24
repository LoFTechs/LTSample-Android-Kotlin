package com.loftechs.sample.common.picker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.loftechs.sample.R
import com.loftechs.sample.common.picker.PickerAdapter.PickerItemViewHolder
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.model.data.ProfileInfoEntity

class PickerAdapter(
        private val mPresenter: PickerContract.Presenter<PickerContract.View>?,
) : ListAdapter<ProfileInfoEntity, PickerItemViewHolder>(PickerDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerItemViewHolder {
        return PickerItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: PickerItemViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class PickerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IPickerItemView {
        private val avatarView: ImageView = itemView.findViewById(R.id.item_contact_avatar)
        private val title: MaterialTextView = itemView.findViewById(R.id.item_contact_title)
        private val checkBox: CheckBox = itemView.findViewById(R.id.item_contact_checkbox)

        init {
            checkBox.visibility = View.VISIBLE
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                mPresenter?.operateCheck(adapterPosition, isChecked)
            }
        }

        override fun bindAvatar(uri: Uri?) {
            avatarView.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        }

        override fun setTitleText(text: String) {
            title.text = text
        }
    }

    interface IPickerItemView {
        fun bindAvatar(uri: Uri?)
        fun setTitleText(text: String)
    }
}

class PickerDiff : DiffUtil.ItemCallback<ProfileInfoEntity>() {
    override fun areItemsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem == newItem
    }
}