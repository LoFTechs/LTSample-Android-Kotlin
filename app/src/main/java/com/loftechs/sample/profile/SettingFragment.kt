package com.loftechs.sample.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.component.GlideApp
import com.loftechs.sample.utils.CustomUIUtil
import java.io.File

class SettingFragment : AbstractFragment(), SettingContract.View {

    private var mPresenter: SettingContract.Presenter<SettingContract.View>? = null

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mAvatar: ImageView
    private lateinit var mNickname: MaterialTextView

    private lateinit var mMuteTitle: MaterialTextView
    private lateinit var mMuteSwitch: SwitchMaterial
    private lateinit var mNotificationSenderTitle: MaterialTextView
    private lateinit var mNotificationSenderSwitch: SwitchMaterial
    private lateinit var mNotificationContentTitle: MaterialTextView
    private lateinit var mNotificationContentSwitch: SwitchMaterial

    companion object {
        val newInstance: SettingFragment
            get() = SettingFragment()
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: SettingPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
    }

    private fun initView(view: View) {
        mToolbar = view.findViewById(R.id.setting_toolbar)
        mToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        mAvatar = view.findViewById(R.id.setting_avatar)
        mNickname = view.findViewById(R.id.setting_nickname)
        val profileLayout: ConstraintLayout = view.findViewById(R.id.setting_profile_layout)
        profileLayout.setOnClickListener {
            changeFragment(SetProfileFragment.newInstance(), mPresenter?.getSetProfileBundle(requireArguments()))
        }

        val muteLayout: ConstraintLayout = view.findViewById(R.id.setting_mute)
        mMuteTitle = muteLayout.findViewById(R.id.item_setting_title)
        mMuteSwitch = muteLayout.findViewById(R.id.item_setting_switch)
        mMuteSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter?.setMute(isChecked)
        }

        val notificationSenderLayout: ConstraintLayout = view.findViewById(R.id.setting_notification_sender)
        mNotificationSenderTitle = notificationSenderLayout.findViewById(R.id.item_setting_title)
        mNotificationSenderSwitch = notificationSenderLayout.findViewById(R.id.item_setting_switch)
        mNotificationSenderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter?.enableNotificationDisplay(isChecked, mNotificationContentSwitch.isChecked)
        }

        val notificationContentLayout: ConstraintLayout = view.findViewById(R.id.setting_notification_content)
        mNotificationContentTitle = notificationContentLayout.findViewById(R.id.item_setting_title)
        mNotificationContentSwitch = notificationContentLayout.findViewById(R.id.item_setting_switch)
        mNotificationContentSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter?.enableNotificationDisplay(mNotificationSenderSwitch.isChecked, isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    override fun setDefaultAvatarView() {
        val size = CustomUIUtil.convertDpToPixel(requireContext(), 64)
        GlideApp.with(mAvatar)
                .load(R.drawable.ic_profile)
                .circleCrop()
                .override(size)
                .into(mAvatar)
    }

    override fun setAvatarView(avatarFile: File) {
        val size = CustomUIUtil.convertDpToPixel(requireContext(), 64)
        GlideApp.with(mAvatar)
                .load(avatarFile)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(size)
                .into(mAvatar)
    }

    override fun setNicknameText(nicknameText: String) {
        mNickname.text = nicknameText
    }

    override fun setMuteTitleText(textResourceID: Int) {
        mMuteTitle.text = getString(textResourceID)
    }

    override fun setMuteStatus(isMute: Boolean) {
        mMuteSwitch.isChecked = isMute
    }

    override fun setDisplaySenderTitleText(textResourceID: Int) {
        mNotificationSenderTitle.text = getString(textResourceID)
    }

    override fun setDisplaySenderStatus(showDisplaySender: Boolean) {
        mNotificationSenderSwitch.isChecked = showDisplaySender
    }

    override fun setDisplayContentTitleText(textResourceID: Int) {
        mNotificationContentTitle.text = getString(textResourceID)
    }

    override fun setDisplayContentStatus(showDisplayContent: Boolean) {
        mNotificationContentSwitch.isChecked = showDisplayContent
    }
}