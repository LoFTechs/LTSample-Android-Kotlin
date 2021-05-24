package com.loftechs.sample.profile

import androidx.annotation.StringRes
import com.loftechs.sample.R

enum class SettingItemType(@StringRes val titleResourceID: Int) {

    ITEM_SETTING_MUTE(R.string.setting_item_mute) {
        override fun setTitleText(view: SettingContract.View?) {
            view?.setMuteTitleText(titleResourceID)
        }
    },
    ITEM_SETTING_DISPLAY_SENDER(R.string.setting_item_display_sender) {
        override fun setTitleText(view: SettingContract.View?) {
            view?.setDisplaySenderTitleText(titleResourceID)
        }
    },
    ITEM_SETTING_DISPLAY_CONTENT(R.string.setting_item_display_content) {
        override fun setTitleText(view: SettingContract.View?) {
            view?.setDisplayContentTitleText(titleResourceID)
        }
    };

    abstract fun setTitleText(view: SettingContract.View?)
}