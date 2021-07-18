package com.loftechs.sample.model

import android.net.Uri
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.writeToImage
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.RemoteFileManager
import com.loftechs.sample.model.api.UserProfileManager
import com.loftechs.sample.model.api.isDone
import com.loftechs.sample.utils.FileUtil
import com.loftechs.sdk.im.channels.LTChannelProfileFileResponse
import com.loftechs.sdk.im.users.LTUserProfileFileResponse
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.storage.LTStorageResult
import io.reactivex.Observable
import java.io.File

object AvatarManager {

    fun loadAvatar(receiverID: String, fileInfo: LTFileInfo?): Observable<Uri> {
        return fileInfo?.let {
            if(!it.isExist) {
                Observable.error(Throwable("loadAvatar fileInfo is not exist."))
            } else {
                val localFile = FileUtil.getProfileFile(it.filename, true)
                if (localFile.length() > 0) {
                    logDebug("[local]loadAvatar file(${it.filename})")
                    Observable.just(Uri.fromFile(localFile))
                } else {
                    logDebug("[remote]loadAvatar file(${it.filename})")
                    downloadAvatar(receiverID, it, localFile)
                            .flatMap {
                                if (localFile.length() > 0) {
                                    Observable.just(Uri.fromFile(localFile))
                                } else {
                                    Observable.error(Throwable("Avatar file has problem."))
                                }
                            }
                }
            }
        } ?: run {
            logDebug("loadAvatar fileInfo is null")
            Observable.error(Throwable("loadAvatar fileInfo is null."))
        }
    }

    fun uploadChatAvatar(receiverID: String, chatID: String, uri: Uri): Observable<LTChannelProfileFileResponse> {
        logDebug("uploadChatAvatar id : $chatID, uri : $uri")
        val fileUri = saveAvatarFile(chatID, uri)
        return ChatFlowManager.setChannelAvatar(receiverID, chatID, fileUri)
    }

    fun uploadUserAvatar(receiverID: String, userID: String, uri: Uri): Observable<LTUserProfileFileResponse> {
        logDebug("uploadUserAvatar id : $userID, uri : $uri")
        val fileUri = saveAvatarFile(userID, uri)
        return UserProfileManager.setUserAvatar(receiverID, fileUri)
    }

    private fun downloadAvatar(receiverID: String, fileInfo: LTFileInfo, localFile: File): Observable<LTStorageResult> {
        logDebug("downloadAvatar ++ filename: ${fileInfo.filename}")
        return RemoteFileManager.downloadFile(receiverID, fileInfo, localFile)
                .filter {
                    it.isDone()
                }
    }

    private fun saveAvatarFile(id: String, uri: Uri): Uri {
        val filename = "$id.jpg"
        val originalFile = FileUtil.getProfileFile(filename, true)

        uri.writeToImage(originalFile)
        return Uri.fromFile(originalFile)
    }
}