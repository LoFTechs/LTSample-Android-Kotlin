package com.loftechs.sample.extensions

import android.net.Uri
import android.os.Environment
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.model.MessageFlowManager
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.im.message.*
import com.loftechs.sdk.im.queries.*
import com.loftechs.sdk.listener.LTCallbackObserverListener
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import java.io.File
import java.util.concurrent.TimeUnit


fun MessageFlowManager.allMessage() {
//    // Text Message
//    val transID = Utils.createTransId()
//    val chID = "xxxxxxxx"//真實存在的 ChannelID
//    val chType = LTChannelType.GROUP
//    val tagUsers = arrayOf(
//            LTTagUser.builder()
//                    .userID("userIDA")
//                    .start(0)
//                    .length(4)
//                    .build()).toList()
//    LTTextMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .msgContent("@Paul, How are you?")
//            .tagUsers(tagUsers)
//            .build()
//    // Sticker Message
//    val stickMessage = LTStickerMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .msgContent("690d1a03-b9c5-ae30-046b-24936dc97c46,3013,2")
//            .build()
//    // Image Message
//    val imageUri: Uri = Uri.parse(Environment.getRootDirectory().toString()+ File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//    val thUri: Uri = Uri.parse(Environment.getRootDirectory().toString()+ File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["width"] = 360
//    extInfoMap["height"] = 422
//    val imageMessage = LTImageMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .imageUri(imageUri)
//            .thumbnailUri(thUri)
//            .extInfo(extInfoMap)
//            .displayFileName("android.png")
//            .build()
//    // Video Message
//    val videoUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/test.mp4")
//    val thumbnailUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/images.jpeg")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["width"] = 360
//    extInfoMap["height"] = 422
//    val videoMessage = LTVideoMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .videoUri(videoUri)
//            .thumbnailUri(thumbnailUri)
//            .extInfo(extInfoMap)
//            .displayFileName("android.mp4")
//            .build()
//    // Voice Message
//    val voiceUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/27dc2d02-a0df-47d9-9443-f8678356e25e.aac")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["duration"] = "00:05:30"
//    val voiceMessage = LTVoiceMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .voiceUri(voiceUri)
//            .extInfo(extInfoMap)
//            .displayFileName("voice.aac")
//            .build()
//    // Location Message
//    val location = LTLocation.builder()
//            .address("300台灣新竹市東區關新路27號號 17 樓 之 1")
//            .latitude(24.784781264526057)
//            .longitude(121.01793609559535)
//            .build()
//    val locationMessage = LTLocationMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .location(location)
//            .build()
//    // Contact Message
//    val contactUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/27dc2d02-a0df-47d9-9443-f8678356e25e.vcf")
//    val thumbnailUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/images.jpeg")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["displayName"] = "Ann"
//    val contactsMessage = LTContactMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .contactUri(contactUri) // vcard
//            .thumbnailUri(thumbnailUri)
//            .displayFileName("Ann.vcf")
//            .extInfo(extInfoMap)
//            .build()
//    // Document Message
//    val documentUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/27dc2d02-a0df-47d9-9443-f8678356e25e.doc")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["fileSize"] = "5MB"
//    LTDocumentMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .fileUri(documentUri)
//            .displayFileName("Document.doc")
//            .extInfo(extInfoMap)
//            .build()
//    // special Custom Message
////    val attributes:Int = LTMessageAttribute.SAVE_MSG | LTMessageAttribute.SEND_NOTIFICATION;
////
////    LTCustomMessage.builder()
////            .transID(transID)
////            .chID(chID)
////            .chType(chType)
////            .msgContent("{\"title\":\"titleA\",\"body\":\"body!!\",\"note\":\"note\"}")
////            .msgCategory("msg_custom")
////            .attributes(attributes)
////            .extInfo(extInfoMap)
////            .build());
//
//    // Relpy Messages
//    val replyMessage = LTReplyMessage.builder()
//            .msgID("cec95048-9e62-11ea-9c63-599b39045450")
//            .msgType(LTMessageType.TYPE_TEXT)
//            .sendTime(1590054366458L)
//            .senderID("senderUserID")
//            .senderNickname("Ann")
//            .build()
//    val imageUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//    val thUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//    val extInfoMap: MutableMap<String, Any> = HashMap()
//    extInfoMap["width"] = 360
//    extInfoMap["height"] = 422
//    val imageMessage = LTImageMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .imageUri(imageUri)
//            .thumbnailUri(thUri)
//            .extInfo(extInfoMap)
//            .displayFileName("android.png")
//            .replyMessage(replyMessage)
//            .parentMsgID("cec95048-9e62-11ea-9c63-599b39045450")
//            .build()
//    // 文字訊息(子訊息) 回覆 圖片訊息(母訊息)
//    val replyMessage = LTReplyMessage.builder()
//            .msgID("9a3a8afa-06f8-11eb-9c63-1a2e0f0676dd")
//            .msgType(LTMessageType.TYPE_IMAGE)
//            .sendTime(1601895037417L)
//            .senderID("senderUserID")
//            .senderNickname("Ann")
//            .fileInfo(fileInfo)
//            .thumbnailFileInfo(thumbnailFileInfo)
//            .build()
//    val textMessage = LTTextMessage.builder()
//            .transID(transID)
//            .chID(chID)
//            .chType(chType)
//            .msgContent("1234")
//            .replyMessage(replyMessage)
//            .parentMsgID("07a9dd94-d5f7-11ea-9c63-1a2d5b06220d")
//            .build()
}

fun MessageFlowManager.sendMessage(receiverID: String)
        : Observable<LTSendMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val chType = LTChannelType.GROUP
                    val textMessage: LTTextMessage = LTTextMessage.builder()
                            .transID(transID)
                            .chID(chID)
                            .chType(chType)
                            .msgContent("1234")
                            .build()
                    it.messageHelper.sendMessage(
                            textMessage,
                            object : LTCallbackResultListener<LTSendMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTSendMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.sendBroadcastMessages(receiverID: String)
        : Observable<LTBroadcastMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    // Text Message
                    val textMessage = LTTextMessage.builder()
                            .transID(Utils.createTransId())
                            .msgContent("message")
                            .build()
                    // Stick Message
                    val stickMessage: LTStickerMessage = LTStickerMessage.builder()
                            .transID(Utils.createTransId())
                            .msgContent("690d1a03-b9c5-ae30-046b-24936dc97c46,3013,2")
                            .build()
                    // Image Message
                    val imageUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
                    val thUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
                    val extInfoMap: MutableMap<String, Any> = HashMap()
                    extInfoMap["width"] = 360
                    extInfoMap["height"] = 422
                    val imageMessage = LTImageMessage.builder()
                            .transID(Utils.createTransId())
                            .imageUri(imageUri)
                            .thumbnailUri(thUri)
                            .extInfo(extInfoMap)
                            .displayFileName("android.png")
                            .build()

                    val messages: MutableList<LTMessage> = ArrayList()
                    messages.add(textMessage)
                    messages.add(stickMessage)
                    messages.add(imageMessage)

                    val chIDs: MutableList<String> = ArrayList()
                    chIDs.add(chID)
                    it.messageHelper.broadcastMessage(
                            transID,
                            messages,
                            chIDs,
                            object : LTCallbackResultListener<LTBroadcastMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTBroadcastMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.sendScheduledMessages(receiverID: String)
        : Observable<LTScheduledMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val timeToSend = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)
                    // Text Message
                    val textMessage = LTTextMessage.builder()
                            .transID(Utils.createTransId())
                            .msgContent("message")
                            .build()
                    // Stick Message
                    val stickMessage: LTStickerMessage = LTStickerMessage.builder()
                            .transID(Utils.createTransId())
                            .msgContent("690d1a03-b9c5-ae30-046b-24936dc97c46,3013,2")
                            .build()
                    // Image Message
                    val imageUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
                    val thUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
                    val extInfoMap: MutableMap<String, Any> = HashMap()
                    extInfoMap["width"] = 360
                    extInfoMap["height"] = 422
                    val imageMessage = LTImageMessage.builder()
                            .transID(Utils.createTransId())
                            .imageUri(imageUri)
                            .thumbnailUri(thUri)
                            .extInfo(extInfoMap)
                            .displayFileName("android.png")
                            .build()

                    val messages: MutableList<LTMessage> = ArrayList()
                    messages.add(textMessage)
                    messages.add(stickMessage)
                    messages.add(imageMessage)

                    val chIDs: MutableList<String> = ArrayList()
                    chIDs.add(chID)
                    it.messageHelper.scheduledMessage(
                            transID,
                            messages,
                            chIDs,
                            timeToSend,
                            object : LTCallbackResultListener<LTScheduledMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTScheduledMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.sendForwardMessages(receiverID: String)
        : Observable<LTForwardMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chIDA = "xxxxxxxx"//真實存在的 ChannelID
                    val chIDB = "xxxxxxxx"//真實存在的 ChannelID

                    val messages: MutableList<String> = ArrayList()
                    messages.add("msgIDA") //真實存在的 msgID
                    messages.add("msgIDB") //真實存在的 msgID
                    messages.add("msgIDC") //真實存在的 msgID

                    val chIDs: MutableList<String> = ArrayList()
                    chIDs.add(chIDA)
                    chIDs.add(chIDB)
                    it.messageHelper.forwardMessage(
                            transID,
                            messages,
                            chIDs,
                            object : LTCallbackResultListener<LTForwardMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTForwardMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.createVote(receiverID: String)
        : Observable<LTCreateVoteResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "chID" //真實存在的 ChannelID
                    val subject = "subject"
                    val chType = LTChannelType.GROUP
                    val vote1 = LTVoteOption()
                    vote1.msgContent = "option1"
                    vote1.msgType = LTMessageType.TYPE_VOTE_TEXT
                    val vote2 = LTVoteOption()
                    vote2.msgContent = "option2"
                    vote2.msgType = LTMessageType.TYPE_VOTE_TEXT
                    val voteOptions: List<LTVoteOption> = listOf(vote1, vote2)
                    val fileSize = 0L
                    val timeToStartVote = System.currentTimeMillis()
                    val timeToCloseVote = System.currentTimeMillis() + 86400 * 7
                    it.messageHelper.createVote(
                            transID,
                            chID,
                            chType,
                            subject,
                            voteOptions,
                            fileSize,
                            timeToStartVote,
                            timeToCloseVote,
                            object : LTCallbackResultListener<LTCreateVoteResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTCreateVoteResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.castVote(receiverID: String)
        : Observable<LTCastVoteResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val optionMsgID = "optionMsgID" //投票選項的 MsgID
                    it.messageHelper.castVote(
                            transID,
                            optionMsgID,
                            object : LTCallbackResultListener<LTCastVoteResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTCastVoteResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.sendQueryMessage(
        receiverID: String
): Observable<LTQueryMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val markTS = 1608797454000L
                    val afterN = 30
                    it.messageHelper.queryMessage(
                            transID,
                            chID,
                            markTS,
                            afterN,
                            object : LTCallbackResultListener<LTQueryMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.queryVotingList(
        receiverID: String
): Observable<LTQueryVoteResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val markTS = 1590054366458L
                    val afterN = 30
                    it.messageHelper.queryVotingList(
                            transID,
                            chID,
                            markTS,
                            afterN,
                            object : LTCallbackResultListener<LTQueryVoteResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryVoteResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.queryVotingOptionList(
        receiverID: String
): Observable<LTQueryVoteOptionResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val messages: MutableList<String> = ArrayList()
                    messages.add("msgIDA") //真實存在的 msgID
                    messages.add("msgIDB") //真實存在的 msgID
                    messages.add("msgIDC") //真實存在的 msgID
                    it.messageHelper.queryVotingOptionList(
                            transID,
                            messages,
                            object : LTCallbackResultListener<LTQueryVoteOptionResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryVoteOptionResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.markRead(
        receiverID: String
): Observable<LTMarkReadResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val sendTime = 1608797454000L //存在於此 Channel 的某訊息的 sendTime
                    it.messageHelper.markRead(
                            transID,
                            chID,
                            sendTime,
                            object : LTCallbackResultListener<LTMarkReadResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTMarkReadResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.markReadNews(
        receiverID: String
): Observable<LTMarkReadNewsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val markTS = 1608797454000L //存在於此 Channel 的某訊息的 sendTime
                    val msgID = "xxxxxxxx" //真實存在的 msgID
                    it.messageHelper.markReadNews(
                            transID,
                            chID,
                            msgID,
                            markTS,
                            object : LTCallbackResultListener<LTMarkReadNewsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTMarkReadNewsResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.queryMessageReadCount(
        receiverID: String
): Observable<LTQueryMessageReadCountResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val messages: MutableList<String> = ArrayList()
                    messages.add("msgIDA") //真實存在的 msgID
                    messages.add("msgIDB") //真實存在的 msgID
                    messages.add("msgIDC") //真實存在的 msgID
                    it.messageHelper.queryMessageReadCount(
                            transID,
                            messages,
                            object : LTCallbackResultListener<LTQueryMessageReadCountResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryMessageReadCountResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.queryMessageReadUsers(
        receiverID: String
): Observable<LTQueryMessageReadUsersResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val msgID = "xxxxxxxx" //真實存在的 msgID
                    val lastReadTime = 0L
                    val count = 100
                    it.messageHelper.queryMessageReadUsers(
                            transID,
                            msgID,
                            lastReadTime,
                            count,
                            object : LTCallbackResultListener<LTQueryMessageReadUsersResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryMessageReadUsersResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.deleteMessages(
        receiverID: String
): Observable<LTDeleteMessagesResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val messages: MutableList<String> = ArrayList()
                    messages.add("msgIDA") //真實存在的 msgID
                    messages.add("msgIDB") //真實存在的 msgID
                    messages.add("msgIDC") //真實存在的 msgID
                    it.messageHelper.deleteMessages(
                            transID,
                            messages,
                            object : LTCallbackResultListener<LTDeleteMessagesResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTDeleteMessagesResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.deleteChannelMessages(
        receiverID: String
): Observable<LTDeleteChannelMessageResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    it.messageHelper.deleteChannelMessages(
                            transID,
                            chID,
                            object : LTCallbackResultListener<LTDeleteChannelMessageResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTDeleteChannelMessageResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.deleteAllMessages(
        receiverID: String
): Observable<LTDeleteAllMessagesResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    it.messageHelper.deleteAllMessages(
                            transID,
                            object : LTCallbackResultListener<LTDeleteAllMessagesResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTDeleteAllMessagesResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun MessageFlowManager.recallMessages(
        receiverID: String
): Observable<LTRecallMessagesResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val messages: MutableList<String> = ArrayList()
                    messages.add("msgIDA") //真實存在的 msgID
                    messages.add("msgIDB") //真實存在的 msgID
                    messages.add("msgIDC") //真實存在的 msgID
                    val silentMode = false
                    it.messageHelper.recallMessage(
                            transID,
                            messages,
                            silentMode,
                            object : LTCallbackObserverListener<LTRecallMessagesResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onNext(result: LTRecallMessagesResponse) {
                                    emmit.onNext(result)
                                }

                                override fun onComplete() {
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

// Special
//fun MessageFlowManager.sendMessageByGroup(receiverID: String)
//        : Observable<LTSendMessageResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chID = "xxxxxxxx"//真實存在的 ChannelID
//                    val chType = LTChannelType.GROUP
//                    val textMessage: LTTextMessage = LTTextMessage.builder()
//                            .transID(transID)
//                            .chID(chID)
//                            .chType(chType)
//                            .msgContent("1234")
//                            .groupID("groupID") //如需針對 group 發訊才需設置
//                            .build()
//                    it.messageHelper.sendMessage(
//                            textMessage,
//                            object : LTCallbackResultListener<LTSendMessageResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTSendMessageResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}

//fun MessageFlowManager.sendBroadcastMessagesByGroup(receiverID: String)
//        : Observable<LTBroadcastMessageResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chID = "xxxxxxxx"//真實存在的 ChannelID
//                    // Text Message
//                    val textMessage = LTTextMessage.builder()
//                            .transID(Utils.createTransId())
//                            .msgContent("message")
//                            .build()
//                    // Stick Message
//                    val stickMessage: LTStickerMessage = LTStickerMessage.builder()
//                            .transID(Utils.createTransId())
//                            .msgContent("690d1a03-b9c5-ae30-046b-24936dc97c46,3013,2")
//                            .groupID("groupID") //如需針對 group 發訊才需設置
//                            .build()
//                    // Image Message
//                    val imageUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//                    val thUri = Uri.parse(Environment.getRootDirectory().toString() + File.separator.toString() + "Download/ef05f74a-90f7-4f69-94cb-d588fa435bae.png")
//                    val extInfoMap: MutableMap<String, Any> = HashMap()
//                    extInfoMap["width"] = 360
//                    extInfoMap["height"] = 422
//                    val imageMessage = LTImageMessage.builder()
//                            .transID(Utils.createTransId())
//                            .imageUri(imageUri)
//                            .thumbnailUri(thUri)
//                            .extInfo(extInfoMap)
//                            .displayFileName("android.png")
//                            .build()
//
//                    val messages: MutableList<LTMessage> = ArrayList()
//                    messages.add(textMessage)
//                    messages.add(stickMessage)
//                    messages.add(imageMessage)
//
//                    val chIDs: MutableList<String> = ArrayList()
//                    chIDs.add(chID)
//                    it.messageHelper.sendBroadcastMessages(
//                            transID,
//                            chIDs,
//                            messages,
//                            object : LTCallbackResultListener<LTBroadcastMessageResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTBroadcastMessageResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}

//fun MessageFlowManager.sendQueryMessageByGroup(
//        receiverID: String
//): Observable<LTQueryMessageResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chID = "xxxxxxxx"//真實存在的 ChannelID
//                    val markTS = 1608797454000L
//                    val groupID = "groupID"//針對特定groupID
//                    val afterN = 30
//                    val msgCategory = ""
//
//                    it.messageHelper.queryMessage(
//                            transID,
//                            chID,
//                            markTS,
//                            afterN,
//                            object : LTCallbackResultListener<LTQueryMessageResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTQueryMessageResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}

//fun MessageFlowManager.queryMessageReadCountByMode(
//        receiverID: String
//): Observable<LTQueryMessageReadCountResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val messages: MutableList<String> = ArrayList()
//                    messages.add("msgIDA") //真實存在的 msgID
//                    messages.add("msgIDB") //真實存在的 msgID
//                    messages.add("msgIDC") //真實存在的 msgID
//                    it.messageHelper.queryMessageReadCount(
//                            transID,
//                            messages,
//                            object : LTCallbackResultListener<LTQueryMessageReadCountResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTQueryMessageReadCountResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}

//fun MessageFlowManager.queryMessageUnreadUsers(
//        receiverID: String
//): Observable<LTQueryMessageReadUsersResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val msgID = "xxxxxxxx" //真實存在的 msgID
//                    val lastReadTime = 0L
//                    val count = 100
//                    it.messageHelper.queryMessageUnreadUsers(
//                            transID,
//                            msgID,
//                            lastReadTime,
//                            count,
//                            object : LTCallbackResultListener<LTQueryMessageReadUsersResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTQueryMessageReadUsersResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}


