package com.loftechs.sample.extensions

import android.net.Uri
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sdk.im.channels.*
import com.loftechs.sdk.im.message.LTMemberModel
import com.loftechs.sdk.im.queries.*
import com.loftechs.sdk.listener.LTCallbackObserverListener
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable

fun ChatFlowManager.createMyFileChannel(receiverID: String
): Observable<LTCreateChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    it.channelHelper.createMyFileChannel(
                            transID,
                            object : LTCallbackResultListener<LTCreateChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTCreateChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.createSingleChannel(receiverID: String
): Observable<LTCreateChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val memberModel = LTMemberModel.builder()
                            .userID("userID")
                            .chNickname("Paul")
                            .build()
                    it.channelHelper.createSingleChannel(
                            transID,
                            memberModel,
                            object : LTCallbackResultListener<LTCreateChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTCreateChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.createGroupChannel(receiverID: String
): Observable<LTCreateChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = Utils.createTransId()
                    val subject = "Channel Subject"
                    val memberModelSet: MutableSet<LTMemberModel> = HashSet()
                    var memberModel = LTMemberModel.builder()
                            .userID("userIDA")
                            .chNickname("Paul")
                            .roleID(LTChannelRole.PARTICIPANT)
                            .build()
                    memberModelSet.add(memberModel)

                    memberModel = LTMemberModel.builder()
                            .userID("userIDB")
                            .chNickname("Hugo")
                            .roleID(LTChannelRole.PARTICIPANT)
                            .build()
                    memberModelSet.add(memberModel)
                    it.channelHelper.createGroupChannel(
                            transID,
                            chID, subject,
                            memberModelSet,
                            object : LTCallbackResultListener<LTCreateChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTCreateChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelMute(receiverID: String
): Observable<LTChannelPreferenceResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val isMute = true
                    it.channelHelper.setChannelMute(
                            transID,
                            chID,
                            isMute,
                            object : LTCallbackResultListener<LTChannelPreferenceResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelPreferenceResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelRingTone(receiverID: String
): Observable<LTChannelPreferenceResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val ringToneID = "xxxxxxxx.mp3"
                    it.channelHelper.setChannelRingTone(
                            transID,
                            chID,
                            ringToneID,
                            object : LTCallbackResultListener<LTChannelPreferenceResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelPreferenceResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelUserNickname(receiverID: String
): Observable<LTChannelPreferenceResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val nickname = "new nickname"
                    it.channelHelper.setChannelUserNickname(
                            transID,
                            chID,
                            nickname,
                            object : LTCallbackResultListener<LTChannelPreferenceResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelPreferenceResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelSubject(receiverID: String
): Observable<LTChannelProfileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val subject = "new subject"
                    it.channelHelper.setChannelSubject(
                            transID,
                            chID,
                            subject,
                            object : LTCallbackResultListener<LTChannelProfileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelProfileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelAvatar(receiverID: String
): Observable<LTChannelProfileFileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val avatarPath = Uri.parse("xxxx/xxxx/xxxx.jpg")//avatar 本地路徑
                    it.channelHelper.setChannelAvatar(
                            transID,
                            chID,
                            avatarPath,
                            object : LTCallbackResultListener<LTChannelProfileFileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelProfileFileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.deleteChannelAvatar(receiverID: String
): Observable<LTChannelProfileFileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val fileinfo = LTFileInfo()
//                            channelProfileResponse.getChannelProfile().get("profileImageFileInfo")
                    it.channelHelper.deleteChannelAvatar(
                            transID,
                            chID,
                            fileinfo,
                            object : LTCallbackResultListener<LTChannelProfileFileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelProfileFileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setChannelCustomAttr(receiverID: String
): Observable<LTChannelProfileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val chat_highline = 1 shl 0
                    val chat_encrypt = 1 shl 1
                    val customAttr = chat_highline or chat_encrypt
                    it.channelHelper.setChannelCustomAttr(
                            transID,
                            chID,
                            customAttr,
                            object : LTCallbackResultListener<LTChannelProfileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTChannelProfileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannel(receiverID: String
): Observable<LTQueryChannelsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val withMembers = true //是否取得 member 資訊
                    it.channelHelper.queryChannel(
                            transID,
                            chID,
                            withMembers,
                            object : LTCallbackObserverListener<LTQueryChannelsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onNext(result: LTQueryChannelsResponse) {
                                    emmit.onNext(result)
                                }

                                override fun onComplete() {
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelList(receiverID: String
): Observable<LTQueryChannelsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chTypes = arrayListOf(LTChannelType.SINGLE, LTChannelType.GROUP)
                    val batchCount = 30
                    val withMembers = true //是否取得 member 資訊
                    it.channelHelper.queryChannel(
                            transID,
                            chTypes,
                            withMembers,
                            batchCount,
                            object : LTCallbackObserverListener<LTQueryChannelsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onNext(result: LTQueryChannelsResponse) {
                                    emmit.onNext(result)
                                }

                                override fun onComplete() {
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelListWithLastUpdateTime(receiverID: String
): Observable<LTQueryChannelsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val lastUpdateTime = 1608797454000
                    val batchCount = 30
                    val withMembers = true //是否取得 member 資訊
                    it.channelHelper.queryChannel(
                            transID,
                            lastUpdateTime,
                            withMembers,
                            batchCount,
                            object : LTCallbackObserverListener<LTQueryChannelsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onNext(result: LTQueryChannelsResponse) {
                                    emmit.onNext(result)
                                }

                                override fun onComplete() {
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryCorpChannelListWithLastUpdateTime(receiverID: String
): Observable<LTQueryChannelsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val lastUpdateTime = 1608797454000
                    it.channelHelper.queryCorpChannelList(
                            transID,
                            lastUpdateTime,
                            object : LTCallbackResultListener<LTQueryChannelsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryChannelsResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelsReadTime(receiverID: String
): Observable<LTQueryChannelsReadTimeResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    it.channelHelper.queryChannelReadTime(
                            transID,
                            chID,
                            object : LTCallbackResultListener<LTQueryChannelsReadTimeResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryChannelsReadTimeResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelsReadTimeByLastChID(receiverID: String
): Observable<LTQueryChannelsReadTimeResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val lastChID = ""// 從頭開始的話使用空字串
                    val lastChType = LTChannelType.UNKNOWN// 從頭開始的話使用 LTChannelTypeUnknown
                    val count = 100// 一次取的最大 channel 個數，建議為 100
                    it.channelHelper.queryChannelsReadTime(
                            transID,
                            lastChID,
                            lastChType,
                            count,
                            object : LTCallbackResultListener<LTQueryChannelsReadTimeResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryChannelsReadTimeResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryUnreadChannels(receiverID: String
): Observable<LTQueryUnreadChannelsResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    it.channelHelper.queryUnreadChannels(
                            transID,
                            object : LTCallbackResultListener<LTQueryUnreadChannelsResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryUnreadChannelsResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelReadInfo(receiverID: String
): Observable<LTQueryChannelReadInfoResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    it.channelHelper.queryChannelReadInfo(
                            transID,
                            chID,
                            object : LTCallbackResultListener<LTQueryChannelReadInfoResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryChannelReadInfoResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.dismissChannel(receiverID: String
): Observable<LTDismissChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    it.channelHelper.dismissChannel(
                            transID,
                            chID,
                            object : LTCallbackResultListener<LTDismissChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTDismissChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.inviteMembers(receiverID: String
): Observable<LTInviteMemberResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val memberModelSet: MutableSet<LTMemberModel> = HashSet()
                    var memberModel = LTMemberModel.builder()
                            .userID("userIDA")
                            .chNickname("Paul")
                            .roleID(LTChannelRole.PARTICIPANT)
                            .build()
                    memberModelSet.add(memberModel)
                    memberModel = LTMemberModel.builder()
                            .userID("userIDB")
                            .chNickname("Hugo")
                            .roleID(LTChannelRole.PARTICIPANT)
                            .build()
                    memberModelSet.add(memberModel)
                    it.channelHelper.inviteMembers(
                            transID,
                            chID,
                            memberModelSet,
                            LTJoinMethod.NORMAL,
                            object : LTCallbackResultListener<LTInviteMemberResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTInviteMemberResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.kickMembers(receiverID: String
): Observable<LTKickMemberResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val memberModelSet: MutableSet<LTMemberModel> = HashSet()
                    var memberModel = LTMemberModel.builder()
                            .userID("userIDA")
                            .build()
                    memberModelSet.add(memberModel)
                    memberModel = LTMemberModel.builder()
                            .userID("userIDB")
                            .build()
                    memberModelSet.add(memberModel)
                    it.channelHelper.kickMembers(
                            transID,
                            chID,
                            memberModelSet,
                            object : LTCallbackResultListener<LTKickMemberResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTKickMemberResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.joinChannel(receiverID: String
): Observable<LTJoinChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val byWho = ""
                    it.channelHelper.joinChannel(
                            transID,
                            chID,
                            LTJoinMethod.NORMAL,
                            byWho,
                            object : LTCallbackResultListener<LTJoinChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTJoinChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.leaveChannel(receiverID: String
): Observable<LTLeaveChannelResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    it.channelHelper.leaveChannel(
                            transID,
                            chID,
                            object : LTCallbackResultListener<LTLeaveChannelResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTLeaveChannelResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.setMemberRole(receiverID: String
): Observable<LTMemberRoleResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val userID = "xxxxxxxx" // 須為 channel member
                    val roleID = LTChannelRole.MODERATOR // 新 roleID
                    it.channelHelper.setMemberRole(
                            transID,
                            chID,
                            userID,
                            roleID,
                            object : LTCallbackResultListener<LTMemberRoleResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTMemberRoleResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun ChatFlowManager.queryChannelMembersByChID(receiverID: String
): Observable<LTQueryChannelMembersResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val chID = "xxxxxxxx"//真實存在的 ChannelID
                    val lastUserID = ""// 從頭開始的話使用空字串
                    val count = 30// 一次取的最大 member 個數，建議為 500 內
                    it.channelHelper.queryChannelMembers(
                            transID,
                            chID,
                            lastUserID,
                            count,
                            object : LTCallbackResultListener<LTQueryChannelMembersResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryChannelMembersResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}


// specail
// createCustomChannel
//fun ChatFlowManager.createCustomChannel(receiverID: String
//): Observable<LTCreateChannelResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val memberModelSet: MutableSet<LTMemberModel> = HashSet()
//                    var memberModel = LTMemberModel.builder()
//                            .userID("userIDA")
//                            .chNickname("Paul")
//                            .roleID(LTChannelRole.PARTICIPANT)
//                            .build()
//                    memberModelSet.add(memberModel)
//
//                    memberModel = LTMemberModel.builder()
//                            .userID("userIDB")
//                            .chNickname("Hugo")
//                            .roleID(LTChannelRole.PARTICIPANT)
//                            .build()
//                    memberModelSet.add(memberModel)
//
//                    it.channelHelper.createChannelWithChannelID(
//                            transID,
//                            memberModel,
//                            object : LTCallbackResultListener<LTCreateChannelResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTCreateChannelResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}
// memo
//fun ChatFlowManager.queryChannelListWithLastMsgTime(receiverID: String
//): Observable<LTQueryChannelsResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chTypes = arrayListOf(LTChannelType.SINGLE, LTChannelType.GROUP)
//                    val lastMsgTime = 1608797454000
//                    val batchCount = 30
//                    val withMembers = true //是否取得 member 資訊
//                    it.channelHelper.queryChannelList(
//                            transID,
//                            chTypes,
//                            lastMsgTime,
//                            withMembers,
//                            batchCount,
//                            object : LTCallbackObserverListener<LTQueryChannelsResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onNext(result: LTQueryChannelsResponse) {
//                                    emmit.onNext(result)
//                                }
//
//                                override fun onComplete() {
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}
// setChannelUserBehavior
//fun ChatFlowManager.setChannelUserBehavior(receiverID: String
//): Observable<LTQueryChannelMembersResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chID = "xxxxxxxx"//真實存在的 ChannelID
//                    val lastUserID = ""// 從頭開始的話使用空字串
//                    val count = 30// 一次取的最大 member 個數，建議為 500 內
//                    it.channelHelper.setChannelUserBehavior(
//                            transID,
//                            chID,
//                            lastUserID,
//                            count,
//                            object : LTCallbackResultListener<LTQueryChannelMembersResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTQueryChannelMembersResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}
//setChannelUserPrivilege
//fun ChatFlowManager.setChannelUserPrivilege(receiverID: String
//): Observable<LTQueryChannelMembersResponse> {
//    return LTSDKManager.getIMManager(receiverID)
//            .flatMap {
//                Observable.create { emmit ->
//                    val transID = Utils.createTransId()
//                    val chID = "xxxxxxxx"//真實存在的 ChannelID
//                    val userID = ""// 從頭開始的話使用空字串
//                    it.channelHelper.setChannelUserPrivilege(
//                            transID,
//                            chID,
//                            lastUserID,
//                            count,
//                            object : LTCallbackResultListener<LTQueryChannelMembersResponse> {
//                                override fun onError(errorInfo: LTErrorInfo) {
//                                    emmit.onError(errorInfo)
//                                }
//
//                                override fun onResult(result: LTQueryChannelMembersResponse) {
//                                    emmit.onNext(result)
//                                    emmit.onComplete()
//                                }
//                            })
//                }
//            }
//}