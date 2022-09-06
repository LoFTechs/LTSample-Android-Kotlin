package com.loftechs.sample.extensions

import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.model.api.RemoteFileManager
import com.loftechs.sdk.im.message.LTImageMessage
import com.loftechs.sdk.im.message.LTMessage
import com.loftechs.sdk.im.message.LTSendMessageResponse
import com.loftechs.sdk.storage.LTStorageAction
import com.loftechs.sdk.storage.LTStorageResult
import io.reactivex.Observable
import java.io.File


fun RemoteFileManager.fileExcute(
        receiverID: String
): Observable<LTStorageResult> {
    return LTSDKManager.getStorageManager(receiverID)
            .flatMap {
                val message: LTMessage = LTSendMessageResponse().message
                val expireMinute = 30
                //原圖
                val fileInfo = (message as LTImageMessage).fileInfo
                val file = File("/output/file/path")
                val action = LTStorageAction.createDownloadFileAction(fileInfo, file, expireMinute)

                //縮圖
                val thumbnailFileInfo = message.thumbnailFileInfo
                val thumbnailFile = File("/output/thumbnailFile/path")
                val actionThumbnail = LTStorageAction.createDownloadFileAction(thumbnailFileInfo, thumbnailFile, expireMinute)

                val actions: MutableList<LTStorageAction> = ArrayList()
                actions.add(action)
                actions.add(actionThumbnail)
                it.execute(actions)
            }
            .map {
                it[0]
            }
            .doOnError { throwable ->
                logError("execute:", throwable)
            }
}