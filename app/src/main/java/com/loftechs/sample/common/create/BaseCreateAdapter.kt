package com.loftechs.sample.common.create

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.loftechs.sample.R
import com.loftechs.sample.common.create.BaseCreateAdapter.ContactItemViewHolder
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sample.utils.CustomUIUtil
import timber.log.Timber

class BaseCreateAdapter(
        private val mPresenter: CreateContract.Presenter<CreateContract.View>?,
) : ListAdapter<ProfileInfoEntity, ContactItemViewHolder>(CreateChannelDiff()) {

    companion object {
        private val TAG = BaseCreateAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        return ContactItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return mPresenter?.getItemType(position)?.ordinal ?: 0
    }

    inner class ContactItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IContactItemView {
        private val avatarView: ImageView = itemView.findViewById(R.id.item_contact_avatar)
        private val title: MaterialTextView = itemView.findViewById(R.id.item_contact_title)

        private val sizeInPixel: Int
            get() {
                return CustomUIUtil.convertDpToPixel(avatarView.context, 40)
            }

        init {
            itemView.setOnClickListener {
                Timber.tag(TAG).d("ContactItemViewHolder onClick ++")
                mPresenter?.operateClick(absoluteAdapterPosition)
            }
        }

        override fun bindAvatar(uri: Uri?) {
            avatarView.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        }

        override fun bindIcon(drawableResourceID: Int) {
            avatarView.background = getBackgroundDrawable(R.color.background_green)
            val src = ContextCompat.getDrawable(avatarView.context, drawableResourceID)
            avatarView.setImageDrawable(InsetDrawable(src, 24))
        }

        private fun getBackgroundDrawable(colorResID: Int): Drawable {
            val temp = GradientDrawable()
            temp.setColor(ContextCompat.getColor(avatarView.context, colorResID))
            temp.shape = GradientDrawable.OVAL
            temp.setSize(sizeInPixel, sizeInPixel)
            return temp
        }

        override fun setTitleText(text: String) {
            title.text = text
        }
    }

    interface IContactItemView {
        fun bindAvatar(uri: Uri?)
        fun bindIcon(@DrawableRes drawableResourceID: Int)
        fun setTitleText(text: String)
    }
}

class CreateChannelDiff : DiffUtil.ItemCallback<ProfileInfoEntity>() {
    override fun areItemsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProfileInfoEntity, newItem: ProfileInfoEntity): Boolean {
        return oldItem == newItem
    }
}