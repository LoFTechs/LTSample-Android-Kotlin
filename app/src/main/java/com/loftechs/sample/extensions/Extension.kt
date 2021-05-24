package com.loftechs.sample.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sdk.im.message.LTMessageType
import com.loftechs.sdk.im.message.LTMessageType.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

const val REQUEST_INTRO = 1
const val REQUEST_SELECT_IMAGE = 2
const val REQUEST_SELECT_FILE = 3

fun <T> T?.checkNotNull(): T {
    if (this == null) {
        throw NullPointerException()
    }
    return this
}

fun Uri.writeToImage(destFile: File) {
    val openFileDescriptor = SampleApp.context.contentResolver.openFileDescriptor(this, "r")
    val byteArrayOutputStream = ByteArrayOutputStream()
    BitmapFactory.decodeFileDescriptor(openFileDescriptor?.fileDescriptor)
            .compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    FileOutputStream(destFile).apply {
        write(byteArray)
        flush()
        close()
    }
}

fun Uri.writeToFile(destFile: File) {
    val inputStream = SampleApp.context.contentResolver.openInputStream(this)
    val fileOutputStream = FileOutputStream(destFile)
    inputStream?.use {
        fileOutputStream.use {
            inputStream.copyTo(it)
        }
    }
}

fun Uri.getFileName(): String? {
    if (scheme != null && scheme == "file") {
        return lastPathSegment
    }

    val projection: Array<String> = arrayOf(OpenableColumns.DISPLAY_NAME)
    val cursor = SampleApp.context.contentResolver.query(this, projection,
            null, null, null, null)
    cursor.use {
        if (it != null && it.moveToNext()) {
            return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return ""
}

private val FILE_PROVIDER_AUTHORITY = "${SampleApp.context.packageName}.fileprovider"

fun File.getFileProviderUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, this)
    } else {
        Uri.fromFile(this)
    }
}

fun File.remove() {
    if (exists()) {
        delete()
    }
}

fun LTMessageType.getShowMessage(): String {
    return when (this) {
        TYPE_IMAGE -> {
            SampleApp.context.resources.getString(R.string.chat_message_image)
        }
        TYPE_RECALL_MESSAGE -> {
            SampleApp.context.resources.getString(R.string.chat_message_recall)
        }
        TYPE_ANSWER_INVITATION -> {
            SampleApp.context.resources.getString(R.string.chat_message_join)
        }
        TYPE_CREATE_CHANNEL -> {
            SampleApp.context.resources.getString(R.string.chat_message_create_channel)
        }
        TYPE_INVITE_MEMBER -> {
            SampleApp.context.resources.getString(R.string.chat_message_invite_member)
        }
        TYPE_KICK_MEMBERS -> {
            SampleApp.context.resources.getString(R.string.chat_message_kick_member)
        }
        TYPE_LEAVE_CHANNEL -> {
            SampleApp.context.resources.getString(R.string.chat_message_leave_channel)
        }
        TYPE_DISMISS_CHANNEL -> {
            SampleApp.context.resources.getString(R.string.chat_message_dismiss_channel)
        }
        TYPE_SET_CHANNEL_PROFILE -> {
            SampleApp.context.resources.getString(R.string.chat_message_change_channel_profile)
        }
        else -> {
            "[DEBUG] $this"
        }
    }
}

fun ImageView.loadImageWithGlide(drawable: Int, uri: Uri?, isCircle: Boolean) {
    val defaultRequest = Glide.with(SampleApp.context).load(drawable)
            .run {
                if (isCircle) {
                    this.circleCrop()
                } else {
                    this.centerCrop()
                }
            }

    if (uri == null || uri.toString().isEmpty()) {
        defaultRequest.into(this)
        return
    }
    val drawableRequest =
            Glide.with(SampleApp.context)
                    .load(uri)
                    .thumbnail(defaultRequest)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
    drawableRequest.run {
        if (isCircle) {
            this.circleCrop()
        } else {
            this.centerCrop()
        }
    }
    drawableRequest.into(this)
}
