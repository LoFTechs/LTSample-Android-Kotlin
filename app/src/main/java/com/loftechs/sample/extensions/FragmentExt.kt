package com.loftechs.sample.extensions

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.pickImageFromExternal() {
    startActivityForResult(Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }, REQUEST_SELECT_IMAGE)
}

fun Fragment.pickFileFromExternal() {
    startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
    }, REQUEST_SELECT_FILE)
}

fun Fragment.viewPhotoInExternal(uri: Uri) {
    val chooser = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/*"
    }, "Share Image")

    val resInfoList: List<ResolveInfo> = requireActivity().packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

    for (resolveInfo in resInfoList) {
        val packageName: String = resolveInfo.activityInfo.packageName
        requireActivity().grantUriPermission(packageName, uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(chooser)
}