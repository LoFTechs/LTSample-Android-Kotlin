package com.loftechs.sample.model

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loftechs.sample.SampleApp
import com.loftechs.sample.model.data.AccountEntity
import com.loftechs.sample.model.data.ProfileInfoEntity

object PreferenceSetting {

    private const val PREF_ACCOUNT = "pref_account"
    private const val PREF_NICKNAME = "pref_nickname"
    private const val PREF_TOKEN = "pref_token"
    private const val PREF_NOTIFICATION_DISPLAY = "pref_notification_display"
    private const val PREF_NOTIFICATION_CONTENT = "pref_notification_content"
    private const val PREF_NOTIFICATION_MUTE = "pref_notification_mute"
    private const val PREF_PROFILE = "pref_profile"

    private val pref: SharedPreferences
        get() {
            return PreferenceManager.getDefaultSharedPreferences(SampleApp.context)
        }

    var accountMap: MutableMap<String, AccountEntity>
        get() {
            val json = pref.getString(PREF_ACCOUNT, "")
            return if (!json.isNullOrEmpty()) {
                Gson().fromJson(json, object : TypeToken<Map<String, AccountEntity>>() {}.type)
            } else {
                HashMap()
            }
        }
        set(accountMap) {
            pref.edit().putString(PREF_ACCOUNT, Gson().toJson(accountMap)).apply()
        }

    var profileMap: MutableMap<String, ProfileInfoEntity>
        get() {
            val json = pref.getString(PREF_PROFILE, "")
            return if (!json.isNullOrEmpty()) {
                Gson().fromJson(json, object : TypeToken<Map<String, ProfileInfoEntity>>() {}.type)
            } else {
                HashMap()
            }
        }
        set(profileMap) {
            pref.edit().putString(PREF_PROFILE, Gson().toJson(profileMap)).apply()
        }

    var nickname: String
        get() = pref.getString(PREF_NICKNAME, "") ?: ""
        set(nickname) {
            pref.edit().putString(PREF_NICKNAME, nickname).apply()
        }

    var token: String
        get() = pref.getString(PREF_TOKEN, "") ?: ""
        set(token) {
            pref.edit().putString(PREF_TOKEN, token).commit()
        }

    var notificationDisplay: Boolean
        get() = pref.getBoolean(PREF_NOTIFICATION_DISPLAY, true)
        set(enable) {
            pref.edit().putBoolean(PREF_NOTIFICATION_DISPLAY, enable).commit()
        }
    var notificationContent: Boolean
        get() = pref.getBoolean(PREF_NOTIFICATION_CONTENT, true)
        set(enable) {
            pref.edit().putBoolean(PREF_NOTIFICATION_CONTENT, enable).commit()
        }
    var notificationMute: Boolean
        get() = pref.getBoolean(PREF_NOTIFICATION_MUTE, false)
        set(enable) {
            pref.edit().putBoolean(PREF_NOTIFICATION_MUTE, enable).commit()
        }

    fun clearAllPref() {
        pref.edit().clear().apply()
    }
}