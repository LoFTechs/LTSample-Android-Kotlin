package com.loftechs.sample.utils

import android.content.Context
import android.content.pm.PackageManager

object VersionUtil {
    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager
                    .getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // should never happen
            throw RuntimeException("Could not get package name: $e")
        }
    }
}