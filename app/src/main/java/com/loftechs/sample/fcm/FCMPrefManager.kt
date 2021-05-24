package com.loftechs.sample.fcm

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.loftechs.sample.SampleApp

/**
 * Created by annliu on 2017/12/28.
 */
object FCMPrefManager {
    private const val PREF_VERSION_CODE_KEY = "pref_version_code_key"
    private const val PREF_FCM_TOKEN_KEY = "pref_fcm_token_key"
    private const val PREF_FCM_TOKEN_EXPIRE_KEY = "pref_fcm_token_expire_key"

    private val pref: SharedPreferences
        get() {
            return PreferenceManager.getDefaultSharedPreferences(SampleApp.context)
        }

    var appVersionCode: Long
        get() {
            return pref.getLong(PREF_VERSION_CODE_KEY, 0)
        }
        set(versionCode) {
            pref.edit().putLong(PREF_VERSION_CODE_KEY, versionCode).commit()
        }

    var gcmExpire: Long
        get() {
            return pref.getLong(PREF_FCM_TOKEN_EXPIRE_KEY, 0)
        }
        set(gcmExpire) {
            pref.edit().putLong(PREF_FCM_TOKEN_EXPIRE_KEY, gcmExpire).commit()
        }

    var gcmKey: String?
        get() {
            return pref.getString(PREF_FCM_TOKEN_KEY, "")
        }
        set(gcmKey) {
            pref.edit().putString(PREF_FCM_TOKEN_KEY, gcmKey).commit()
        }
}