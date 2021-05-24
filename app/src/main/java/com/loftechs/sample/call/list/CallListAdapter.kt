package com.loftechs.sample.call.list

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

class CallListAdapter(
        private val mPresenter: CallListContract.Presenter<CallListContract.View>?,
) : ListAdapter<CallLogData, CallListAdapter.CallListViewHolder>(AdapterDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallListViewHolder {
        return CallListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_call_list, parent, false))
    }

    override fun onBindViewHolder(holder: CallListViewHolder, position: Int) {
        mPresenter?.onBindViewHolder(holder, position)
    }

    inner class CallListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, IItemView {
        private val displayView: TextView = itemView.findViewById(R.id.nickname)
        private val startTime: TextView = itemView.findViewById(R.id.start_time)
        private val stateType: ImageView = itemView.findViewById(R.id.state_type)
        private val avatarView: ImageView = itemView.findViewById(R.id.profile_image)
        private val callButton: ImageView = itemView.findViewById(R.id.call_type)

        init {
            callButton.setOnClickListener { v: View -> onClick(v) }
        }

        override fun onClick(v: View) {
            mPresenter?.executeCall(getItem(absoluteAdapterPosition))
        }

        override fun bindAvatar(uri: Uri?) {
            avatarView.loadImageWithGlide(R.drawable.ic_profile, uri, true)
        }

        override fun setDisplayName(text: String) {
            displayView.text = text
        }

        override fun setStartTime(text: String) {
            startTime.text = text
        }

        override fun setState(callState: CallState) {
            when (callState) {
                CallState.MISS -> stateType.setImageResource(R.drawable.ic_miss_call_24)
                CallState.OUT -> stateType.setImageResource(R.drawable.ic_call_out_24)
                else -> stateType.setImageResource(R.drawable.ic_call_in_24)
            }
        }
    }

    interface IItemView {
        fun bindAvatar(uri: Uri?)
        fun setDisplayName(text: String)
        fun setStartTime(text: String)
        fun setState(callState: CallState)
    }
}

class AdapterDiff : DiffUtil.ItemCallback<CallLogData>() {
    override fun areItemsTheSame(oldItem: CallLogData, newItem: CallLogData): Boolean {
        return oldItem.callID == newItem.callID
    }

    override fun areContentsTheSame(oldItem: CallLogData, newItem: CallLogData): Boolean {
        return oldItem == newItem
    }
}