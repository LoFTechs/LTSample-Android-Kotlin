package com.loftechs.sample.chat.create.group

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.google.android.material.appbar.MaterialToolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.chat.chatroom.ChatFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.extensions.REQUEST_SELECT_IMAGE
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.pickImageFromExternal
import com.loftechs.sample.main.MainFragment
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sample.utils.KeyboardUtil
import com.loftechs.sdk.im.channels.LTChannelType
import java.util.*


class CreateGroupFragment : AbstractFragment(), CreateGroupContract.View {

    private var mPresenter: CreateGroupContract.Presenter<CreateGroupContract.View>? = null
    private lateinit var mAdapter: CreateGroupAdapter
    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mGroupAvatar: ImageView
    private lateinit var mGroupSubject: EditText

    companion object {
        fun newInstance(): CreateGroupFragment {
            return CreateGroupFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_group, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: CreateGroupPresenter().apply {
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
        mAdapter = CreateGroupAdapter(mPresenter)
        mRecyclerView.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.create_group_recycler_view)
        mToolbar = view.findViewById(R.id.create_group_toolbar)
        mToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.confirm_create_group -> {
                    mPresenter?.createGroup(mGroupSubject.text.toString())
                    true
                }
                else -> false
            }
        }
        mGroupAvatar = view.findViewById(R.id.create_group_avatar)
        mGroupAvatar.setImageResource(R.drawable.ic_add_photo)
        mGroupAvatar.setOnClickListener {
            pickImageFromExternal()
        }
        mGroupSubject = view.findViewById(R.id.create_group_title)
    }

    override fun refreshList(itemList: ArrayList<ProfileInfoEntity>) {
        mAdapter.submitList(itemList)
    }

    override fun gotoChatPage(channelID: String?, channelType: LTChannelType, subject: String, memberCount: Int) {
        KeyboardUtil.hideKeyboard(requireActivity(), mGroupSubject)
        requireActivity().supportFragmentManager.popBackStack(MainFragment::class.java.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        changeFragment(ChatFragment.newInstance, requireArguments().apply {
            putString(IntentKey.EXTRA_CHANNEL_ID, channelID)
            putSerializable(IntentKey.EXTRA_CHANNEL_TYPE, channelType)
            putString(IntentKey.EXTRA_CHANNEL_SUBJECT, subject)
            putInt(IntentKey.EXTRA_CHANNEL_MEMBER_COUNT, memberCount)
        })
    }

    override fun loadAvatar(uri: Uri?) {
        mGroupAvatar.loadImageWithGlide(R.drawable.ic_add_photo_alpha, uri, true)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        logDebug("onActivityResult $requestCode, $data")
        if (requestCode == REQUEST_SELECT_IMAGE) {
            mPresenter?.setProfileImage(data)
        }
    }
}
