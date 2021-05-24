package com.loftechs.sample.chat.settings

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.loftechs.sample.R

class ChatSettingsAdapter(
        private val mPresenter: ChatSettingsContract.Presenter<ChatSettingsContract.View>?,
) : ListAdapter<ChatSettingsData, ChatSettingsAdapter.TitleViewHolder>(ChatSettingsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        return TitleViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_switch_view, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType.ordinal
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IItemView {
        private val titleView: TextView = itemView.findViewById(R.id.main_title)
        private val switchView: SwitchMaterial = itemView.findViewById(R.id.switch_btn)
        private val imageView: ImageView = itemView.findViewById(R.id.image_title)

        init {
            itemView.setOnClickListener {
                mPresenter?.onItemClick(this, getItem(adapterPosition))
            }
            switchView.setOnClickListener {
                mPresenter?.onItemClick(this, getItem(adapterPosition))
            }
        }

        override fun setTitleText(text: String) {
            titleView.text = text
        }

        override fun setTextColor(color: Int) {
            titleView.setTextColor(ContextCompat.getColor(titleView.context, color))
            if (color == R.color.color_red) {
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
            }
        }

        override fun setImageResource(drawable: Int) {
            imageView.setImageResource(drawable)
        }

        override fun switchViewVisibility(visibility: Boolean) {
            if (visibility) {
                switchView.visibility = View.VISIBLE
            } else {
                switchView.visibility = View.GONE
            }
        }

        override fun switchViewIsChecked(checked: Boolean) {
            switchView.isChecked = checked
        }
    }

    interface IItemView {
        fun setTitleText(text: String)
        fun setTextColor(color: Int)
        fun setImageResource(drawable: Int)
        fun switchViewVisibility(visibility: Boolean)
        fun switchViewIsChecked(checked: Boolean)
    }
}

class ChatSettingsDiff : DiffUtil.ItemCallback<ChatSettingsData>() {
    override fun areItemsTheSame(oldItem: ChatSettingsData, newItem: ChatSettingsData): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: ChatSettingsData, newItem: ChatSettingsData): Boolean {
        return oldItem == newItem
    }
}
