package com.loftechs.sample.utils

import android.Manifest

/**
 * Created by annliu on 2018/4/24.
 */
object PermissionUtil {
    val voicePerms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO //不能用 permission_group.MICROPHONE，有些手機會認不出來
    )
}