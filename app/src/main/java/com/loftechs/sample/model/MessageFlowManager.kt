package com.loftechs.sample.model

import android.net.Uri
import com.loftechs.sample.LTSDKManager.getIMManager
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.im.message.*
import com.loftechs.sdk.im.queries.LTQueryMessageResponse
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import java.util.*

object MessageFlowManager {

    fun sendQueryMessage(receiverID: String, chID: String, startTS: Long, afterN: Int): Observable<LTQueryMessageResponse> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.messageHelper.queryMessage(Utils.createTransId(),
                            chID, startTS, afterN)
                }
    }

    fun sendTextMessage(receiverID: String, chID: String, chType: LTChannelType, message: String): Observable<LTSendMessageResponse> {
        val textMessage = LTTextMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .msgContent(message)
                .build()
        return sendMessage(receiverID, textMessage)
    }

    fun sendImageMessage(receiverID: String, chID: String, chType: LTChannelType, uri: Uri, thUri: Uri, displayFileName: String): Observable<LTFileMessageResponse> {
        val imageMessage = LTImageMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .imageUri(uri)
                .thumbnailUri(thUri)
                .displayFileName(displayFileName)
                .build()
        return sendMessage(receiverID, imageMessage)
    }

    fun sendDocumentMessage(receiverID: String, chID: String, chType: LTChannelType, uri: Uri, displayFileName: String): Observable<LTFileMessageResponse> {
        val documentMessage = LTDocumentMessage.builder()
                .chType(chType)
                .chID(chID)
                .transID(Utils.createTransId())
                .fileUri(uri)
                .displayFileName(displayFileName)
                .build()
        return sendMessage(receiverID, documentMessage)
    }

    private fun <T : LTSendMessageResponse> sendMessage(receiverID: String, message: LTMessage): Observable<T> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager -> imManager.messageHelper.sendMessage(message) }
    }

    fun deleteMessage(receiverID: String, msgID: String): Observable<LTDeleteMessagesResponse> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.messageHelper.deleteMessages(Utils.createTransId(), Collections.singletonList(msgID))
                }
    }

    fun recallMessage(receiverID: String, msgID: String): Observable<LTRecallMessagesResponse> {
        val silentMode = false
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.messageHelper.recallMessages(Utils.createTransId(), Collections.singletonList(msgID), silentMode)
                }
    }

    fun markRead(receiverID: String, channelID: String, markReadTime:Long): Observable<LTMarkReadResponse> {
        return getIMManager(receiverID)
                .flatMap {
                    it.messageHelper.markRead(Utils.createTransId(), channelID, markReadTime)
                }
    }
}