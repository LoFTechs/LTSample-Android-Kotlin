package com.loftechs.sample.model.api

import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.extensions.logError
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.storage.LTStorageAction
import com.loftechs.sdk.storage.LTStorageManager.StorageStatus.*
import com.loftechs.sdk.storage.LTStorageResult
import io.reactivex.Observable
import java.io.File

object RemoteFileManager {

    fun downloadFile(receiverID: String, fileInfo: LTFileInfo, file: File): Observable<LTStorageResult> {
        return execute(receiverID, LTStorageAction.createDownloadFileAction(fileInfo, file))
    }

    private fun execute(receiverID: String, action: LTStorageAction): Observable<LTStorageResult> {
        return LTSDKManager.getStorageManager(receiverID)
                .flatMap {
                    it.execute(arrayListOf(action))
                }
                .map {
                    it[0]
                }
                .doOnError { throwable ->
                    logError("execute: $action,", throwable)
                }
    }
}

fun LTStorageResult.isDone(): Boolean {
    return status === UPLOAD_DONE || status === DOWNLOAD_DONE || status === DELETE_DONE
}