package com.loftechs.sample.model.api

import android.net.Uri
import com.loftechs.sample.LTSDKManager.getIMManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.im.channels.LTChannelProfileFileResponse
import com.loftechs.sdk.im.channels.LTChannelResponse
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.im.channels.LTCreateChannelResponse
import com.loftechs.sdk.im.extension.rx.*
import com.loftechs.sdk.im.message.LTDeleteChannelMessageResponse
import com.loftechs.sdk.im.message.LTFileMessageStatus
import com.loftechs.sdk.im.message.LTMemberModel
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable

object ChatFlowManager {

    fun getUserIDFromOneToOneChannel(receiverID: String, channelID: String): String {
        if (channelID.isEmpty()) {
            return ""
        }

        val split = channelID.split(":")
        for (userID in split) {
            if (receiverID != userID) {
                return userID
            }
        }

        return ""
    }

    fun queryChannelListByChannelType(
        receiverID: String,
        channelTypeList: List<LTChannelType>,
    ): Observable<List<LTChannelResponse>> {
        return getIMManager(receiverID)
            .flatMap {
                it.channelHelper.queryChannelList(
                    Utils.createTransId(),
                    channelTypeList, false, 30
                )
            }
            .map {
                it.channels
            }

    }

    fun queryChannelByID(
        receiverID: String,
        id: String,
    ): Observable<LTChannelResponse> {
        return getIMManager(receiverID)
            .flatMap {
                it.channelHelper.queryChannel(
                    Utils.createTransId(),
                    id, false
                )
            }
            .map {
                it.channels[0]
            }

    }

    fun createGroupChannel(
        receiverID: String,
        subject: String,
        memberModels: Set<LTMemberModel>,
    ): Observable<LTCreateChannelResponse> {
        val chID = Utils.createTransId()
        return getIMManager(receiverID)
            .flatMap {
                it.channelHelper.createGroupChannel(
                    Utils.createTransId(),
                    chID,
                    subject,
                    memberModels
                )
            }
    }

    fun createSingleChannel(
        receiverID: String,
        memberModel: LTMemberModel
    ): Observable<LTCreateChannelResponse> {
        return getIMManager(receiverID)
            .flatMap {
                it.channelHelper.createSingleChannel(Utils.createTransId(), memberModel)
            }
    }

    fun setChannelAvatar(
        receiverID: String,
        chID: String,
        uri: Uri
    ): Observable<LTChannelProfileFileResponse> {
        return getIMManager(receiverID)
            .flatMap {
                it.channelHelper.setChannelAvatar(Utils.createTransId(), chID, uri)
            }
            .filter {
                it.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE // upload avatar status is Done
            }

    }

    fun deleteChannelAvatar(
        receiverID: String,
        chID: String
    ): Observable<LTChannelProfileFileResponse> {
        return ProfileInfoManager.getProfileInfoByChatID(receiverID, chID)
            .flatMap {
                getIMManager(receiverID)
                    .flatMap { imManager: LTIMManager ->
                        it.profileFileInfo?.let { it ->
                            imManager.channelHelper.deleteChannelAvatar(
                                Utils.createTransId(),
                                chID,
                                it
                            )
                        }
                    }
            }.filter {
                it.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE // upload avatar status is Done
            }
    }

    fun deleteChannelMessages(
        receiverID: String,
        channelID: String
    ): Observable<LTDeleteChannelMessageResponse> {
        return getIMManager(receiverID)
            .flatMap {
                it.messageHelper.deleteChannelMessages(Utils.createTransId(), channelID)
            }
    }
}