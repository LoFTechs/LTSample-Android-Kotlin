package com.loftechs.sample.chat.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.chat.member.MemberListFragment
import com.loftechs.sample.component.CustomLinearLayoutManager
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.extensions.REQUEST_SELECT_IMAGE
import com.loftechs.sample.extensions.loadImageWithGlide
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.pickImageFromExternal
import com.loftechs.sample.utils.*

class ChatSettingsFragment : AbstractFragment(), ChatSettingsContract.View {

    private var mPresenter: ChatSettingsContract.Presenter<ChatSettingsContract.View>? = null

    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mSettingsAdapter: ChatSettingsAdapter
    private lateinit var avatarView: ImageView
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mAppBarLayout: AppBarLayout

    companion object {
        fun newInstance(): ChatSettingsFragment {
            return ChatSettingsFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_settings, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: ChatSettingsPresenter().apply {
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
        mSettingsAdapter = ChatSettingsAdapter(mPresenter)
        mRecyclerView.enableDecoration()
        mRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mSettingsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.chat_setting_recycler_view)
        mAppBarLayout = view.findViewById(R.id.bar_layout)
        mToolbar = view.findViewById(R.id.top_bar)
        avatarView = view.findViewById(R.id.chat_image)
        mToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        if (mPresenter?.canEditAvatarOrSubject() == false) {
            mToolbar.menu.setGroupVisible(0, false)
        }
        mToolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.edit_subject -> {
                    showEditSubjectDialog()
                    true
                }
                R.id.edit_channel_profile -> {
                    mPresenter?.editAvatar()
                    true
                }
                else -> false
            }
        }
    }

    override fun showEditSubjectDialog() {
        val customLayout: View = layoutInflater
                .inflate(
                        R.layout.dialog_edit_text,
                        null)
        val editText: EditText = customLayout
                .findViewById(
                        R.id.editText)
        editText.setText(mToolbar.title)
        AlertDialog.Builder(requireContext())
                .setTitle(getText(R.string.chat_setting_menu_item_edit_subject))
                .setView(customLayout)
                .setPositiveButton(R.string.common_confirm) { _, _ ->
                    if (editText.toString().isEmpty()) {
                        return@setPositiveButton
                    }
                    mPresenter?.updateChannelSubject(editText.text.toString())
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }

    override fun showEditAvatarDialog() {
        val itemArray = arrayOf(
                requireContext().resources.getString(R.string.chat_setting_channel_profile_change),
                requireContext().resources.getString(R.string.chat_setting_channel_profile_delete)
        )

        AlertDialog.Builder(requireContext())
                .setItems(itemArray) { dialogInterface, position ->
                    dialogInterface.dismiss()
                    if (position == 0) {
                        pickImageFromExternal()
                    } else if (position == 1) {
                        mPresenter?.deleteAvatar()
                    }
                }
                .show()
    }

    override fun pickImage() {
        pickImageFromExternal()
    }

    override fun loadAvatar(uri: Uri?, defaultDrawable: Int) {
        avatarView.loadImageWithGlide(defaultDrawable, uri, false)
    }

    override fun setSubject(subject: String?) {
        mToolbar.title = subject
    }

    override fun finishChat() {
        activity?.onBackPressed()
    }

    override fun refreshList(settingsData: List<ChatSettingsData>) {
        mSettingsAdapter.submitList(settingsData)
    }

    override fun getStringValue(resValue: Int): String {
        return resources.getString(resValue)
    }

    override fun gotoMemberList() {
        changeFragment(MemberListFragment.newInstance(), requireArguments())
    }

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