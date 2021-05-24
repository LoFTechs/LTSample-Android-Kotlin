package com.loftechs.sample.profile

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.PreferenceSetting
import com.loftechs.sample.model.api.UserProfileManager
import com.loftechs.sample.profile.SettingItemType.*
import com.loftechs.sample.utils.FileUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SettingPresenter : SettingContract.Presenter<SettingContract.View> {

    private var mView: SettingContract.View? = null

    private lateinit var mReceiverID: String

    private val mSettingList: ArrayList<SettingItemType> by lazy {
        arrayListOf(ITEM_SETTING_MUTE, ITEM_SETTING_DISPLAY_SENDER, ITEM_SETTING_DISPLAY_CONTENT)
    }

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun create() {
    }

    override fun resume() {
        refreshAvatar()
        refreshNickname()
        refreshSetting()
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: SettingContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun getSetProfileBundle(bundle: Bundle): Bundle {
        return bundle.apply {
            putString(IntentKey.EXTRA_NICKNAME, PreferenceSetting.nickname)
            putBoolean(IntentKey.EXTRA_IS_FROM_SETTING_PAGE, true)
        }
    }

    private fun refreshAvatar() {
        val avatarFile = FileUtil.getProfileFile("$mReceiverID.jpg", false)
        if (avatarFile.length() == 0L) {
            mView?.setDefaultAvatarView()
        } else {
            mView?.setAvatarView(avatarFile)
        }
    }

    private fun refreshNickname() {
        mView?.setNicknameText(PreferenceSetting.nickname)
    }

    private fun refreshSetting() {
        for (settingItemType in mSettingList) {
            settingItemType.setTitleText(mView)
        }

        val subscribe = UserProfileManager.getDeviceNotify(mReceiverID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({ userNotifyData ->
                    mView?.dismissProgressDialog()
                    mView?.setMuteStatus(userNotifyData.isMute)
                    mView?.setDisplaySenderStatus(!userNotifyData.isHidingCaller)
                    mView?.setDisplayContentStatus(!userNotifyData.isHidingContent)
                    PreferenceSetting.notificationMute = userNotifyData.isMute
                    PreferenceSetting.notificationContent = !userNotifyData.isHidingContent
                    PreferenceSetting.notificationDisplay = !userNotifyData.isHidingCaller
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.setting_notification_sync_error)
                    logError("refreshSetting getDeviceNotify", it)
                    it.printStackTrace()
                })
        mDisposable.add(subscribe)
    }

    override fun setMute(enable: Boolean) {
        val subscribe = UserProfileManager.setNotifyMute(mReceiverID, enable)
                .doOnNext {
                    PreferenceSetting.notificationMute = enable
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("setMute onNext: $it")
                }, {
                    logError("setMute", it)
                    it.printStackTrace()
                    mView?.showSnackBar(R.string.setting_set_mute_error)
                })
        mDisposable.add(subscribe)
    }

    override fun enableNotificationDisplay(showSender: Boolean, showContent: Boolean) {
        val subscribe = UserProfileManager.setNotifyPreview(mReceiverID, !showSender, !showContent)
                .doOnNext {
                    PreferenceSetting.notificationDisplay = showSender
                    PreferenceSetting.notificationContent = showContent
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logDebug("enableNotificationDisplay onNext: $it")
                }, {
                    logError("enableNotificationDisplay", it)
                    it.printStackTrace()
                    mView?.showSnackBar(R.string.setting_set_display_error)
                })
        mDisposable.add(subscribe)
    }
}