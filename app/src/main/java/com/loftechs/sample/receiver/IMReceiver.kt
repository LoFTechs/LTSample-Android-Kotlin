package com.loftechs.sample.receiver

import com.loftechs.sample.common.event.*
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.remove
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.utils.FileUtil
import com.loftechs.sdk.im.LTIMManagerListener
import com.loftechs.sdk.im.channels.*
import com.loftechs.sdk.im.message.*
import com.loftechs.sdk.im.users.LTModifyUserProfileResponse
import com.loftechs.sdk.im.users.LTSetUserProfileResponse
import com.loftechs.sdk.listener.LTErrorInfo
import org.greenrobot.eventbus.EventBus

class IMReceiver : LTIMManagerListener() {

    override fun onConnected(userID: String) {
        logDebug("onConnected userID: $userID")
    }

    override fun onDisconnected(userID: String) {
        logDebug("onDisconnected userID: $userID")
    }

    override fun onIncomingJoinChannel(toUserID: String, joinChannelResponse: LTJoinChannelResponse) {
        logDebug("onIncomingAnswerInvitation response: $joinChannelResponse")
        EventBus.getDefault().post(ChatEvent(toUserID, joinChannelResponse.chID))
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, joinChannelResponse.chID, joinChannelResponse))
    }

    override fun onIncomingCreateChannel(toUserID: String, createChannelResponse: LTCreateChannelResponse) {
        logDebug("onIncomingCreateChannel response: $createChannelResponse")
        EventBus.getDefault().post(ChatEvent(toUserID, createChannelResponse.chID))
    }

    override fun onIncomingDismissChannel(toUserID: String, dismissChannelResponse: LTDismissChannelResponse) {
        logDebug("onIncomingDismissChannel response: $dismissChannelResponse")
        EventBus.getDefault().post(ChatCloseEvent(toUserID, dismissChannelResponse.chID))
    }

    override fun onIncomingInviteMember(toUserID: String, inviteMemberResponse: LTInviteMemberResponse) {
        logDebug("onIncomingInviteMember response: $inviteMemberResponse")
        EventBus.getDefault().post(ChatEvent(toUserID, inviteMemberResponse.chID))
        EventBus.getDefault().post(MemberChangedEvent(toUserID, inviteMemberResponse.chID))
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, inviteMemberResponse.chID, inviteMemberResponse))
    }

    override fun onIncomingKickMember(toUserID: String, kickMemberResponse: LTKickMemberResponse) {
        logDebug("onIncomingKickMember response: $kickMemberResponse")
        val members = kickMemberResponse.members
        for (member in members) {
            if (member.userID == toUserID) {
                FileUtil.getProfileFile("${kickMemberResponse.chID}.jpg", false).remove()
                ProfileInfoManager.cleanProfileInfoByID(kickMemberResponse.chID)
                EventBus.getDefault().post(ChatCloseEvent(toUserID, kickMemberResponse.chID))
                return
            }
        }
        EventBus.getDefault().post(MemberChangedEvent(toUserID, kickMemberResponse.chID))
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, kickMemberResponse.chID, kickMemberResponse))
    }

    override fun onIncomingLeaveChannel(toUserID: String, leaveChannelResponse: LTLeaveChannelResponse) {
        logDebug("onIncomingLeaveChannel response: $leaveChannelResponse")
        val members = leaveChannelResponse.members
        for (member in members) {
            if (member.userID == toUserID) {
                FileUtil.getProfileFile("${leaveChannelResponse.chID}.jpg", false).remove()
                ProfileInfoManager.cleanProfileInfoByID(leaveChannelResponse.chID)
                EventBus.getDefault().post(ChatCloseEvent(toUserID, leaveChannelResponse.chID))
                return
            }
        }
        EventBus.getDefault().post(MemberChangedEvent(toUserID, leaveChannelResponse.chID))
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, leaveChannelResponse.chID, leaveChannelResponse))
    }

    override fun onIncomingChannelPreference(toUserID: String, response: LTChannelPreferenceResponse) {
        logDebug("onIncomingSetChannelPreference response: $response")
        EventBus.getDefault().post(ChatEvent(toUserID, response.chID))
    }

    override fun onIncomingChannelProfile(toUserID: String, response: LTChannelProfileResponse) {
        logDebug("onIncomingSetChannelProfile response: $response")
        ProfileInfoManager.cleanProfileInfoByID(response.chID)
        FileUtil.getProfileFile("${response.chID}.jpg", false).remove()
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, response.chID, response))
        EventBus.getDefault().post(ChatEvent(toUserID, response.chID))
    }

    override fun onIncomingChannelUserProfile(toUserID: String, setChannelProfileResponse: LTChannelUserProfileResponse) {
        logDebug("onIncomingSetChannelUserProfile response: $setChannelProfileResponse")
    }

    override fun onIncomingMemberRole(toUserID: String, memberRoleResponse: LTMemberRoleResponse) {
        logDebug("onIncomingSetMemberRole response: $memberRoleResponse")
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, memberRoleResponse.chID, memberRoleResponse))
    }

    override fun onIncomingSetChannelMemberProfile(toUserID: String, setChannelMemberProfileResponse: LTSetChannelMemberProfileResponse) {
        logDebug("onIncomingSetChannelMemberProfile response: $setChannelMemberProfileResponse")
    }

    override fun onIncomingCreatePublicNewsChannel(toUserID: String, createPublicNewsChannelResponse: LTCreateNewsChannelResponse) {
        logDebug("onIncomingCreatePublicNewsChannel response: $createPublicNewsChannelResponse")
    }

    override fun onIncomingCreateCorpNewsChannel(toUserID: String, createCorpNewsChannelResponse: LTCreateNewsChannelResponse) {
        logDebug("onIncomingCreateCorpNewsChannel response: $createCorpNewsChannelResponse")
    }

    override fun onIncomingMessage(toUserID: String, messageResponse: LTMessageResponse) {
        logDebug("onIncomingMessage response: $messageResponse")
    }

    override fun onIncomingSendMessage(toUserID: String, sendMessageResponse: LTSendMessageResponse) {
        logDebug("onIncomingSendMessage response: $sendMessageResponse")
        EventBus.getDefault().post(IncomingMessageEvent(toUserID, sendMessageResponse.chID, sendMessageResponse))
        EventBus.getDefault().post(ChatEvent(toUserID, sendMessageResponse.chID))
    }

    override fun onIncomingScheduledMessage(toUserID: String, scheduledMessageResponse: LTScheduledMessageResponse) {
        logDebug("onIncomingScheduledMessage response: $scheduledMessageResponse")
    }

    override fun onIncomingScheduledVoteResponse(toUserID: String, scheduledVoteResponse: LTScheduledVoteResponse) {
        logDebug("onIncomingScheduledVoteResponse response: $scheduledVoteResponse")
    }

    override fun onIncomingScheduledInDueTimeMessage(toUserID: String, scheduledInDueTimeMessageResponse: LTScheduledInDueTimeMessageResponse) {
        logDebug("onIncomingScheduledInDueTimeMessage response: $scheduledInDueTimeMessageResponse")
    }

    override fun onIncomingMarkRead(toUserID: String, markReadResponse: LTMarkReadResponse) {
        logDebug("onIncomingMarkRead response: $markReadResponse")
        EventBus.getDefault().post(MarkReadEvent(toUserID, markReadResponse.chID))
    }

    override fun onIncomingMarkReadNews(toUserID: String, markReadNewsResponse: LTMarkReadNewsResponse) {
        logDebug("onIncomingMarkReadNews response: $markReadNewsResponse")
    }

    override fun onIncomingCreateVoteMessage(toUserID: String, createVoteResponse: LTCreateVoteResponse) {
        logDebug("onIncomingCreateVoteMessage response: $createVoteResponse")
    }

    override fun onIncomingCastVoteMessage(toUserID: String, castVoteResponse: LTCastVoteResponse) {
        logDebug("onIncomingCastVoteMessage response: $castVoteResponse")
    }

    override fun onIncomingDeleteAllMessages(toUserID: String, deleteAllMessagesResponse: LTDeleteAllMessagesResponse) {
        logDebug("onIncomingDeleteAllMessages response: $deleteAllMessagesResponse")
    }

    override fun onIncomingDeleteChannelMessage(toUserID: String, deleteChannelMessageResponse: LTDeleteChannelMessageResponse) {
        logDebug("onIncomingDeleteChannelMessage response: $deleteChannelMessageResponse")
    }

    override fun onIncomingDeleteMessages(toUserID: String, deleteMessagesResponse: LTDeleteMessagesResponse) {
        logDebug("onIncomingDeleteMessages response: $deleteMessagesResponse")
        EventBus.getDefault().post(UpdateMessageEvent(toUserID, deleteMessagesResponse.chID))
    }

    override fun onIncomingRecallMessage(toUserID: String, recallMessagesResponse: LTRecallMessagesResponse) {
        logDebug("onIncomingRecallMessage response: $recallMessagesResponse")
        EventBus.getDefault().post(UpdateMessageEvent(toUserID, recallMessagesResponse.chID))
    }

    override fun onIncomingNewsMessage(toUserID: String, messageResponse: LTNewsMessageResponse) {
        logDebug("onIncomingNewsMessage response: $messageResponse")
    }

    override fun onIncomingSetUserProfile(toUserID: String, setUserProfileResponse: LTSetUserProfileResponse) {
        logDebug("onIncomingSetUserProfile response: $setUserProfileResponse")
        ProfileInfoManager.cleanProfileInfoByID(toUserID)
        FileUtil.getProfileFile("${toUserID}.jpg", false).remove()
        EventBus.getDefault().post(UserProfileChangeEvent(toUserID, toUserID))
    }

    override fun onIncomingModifyUserProfile(toUserID: String, modifyUserProfileResponse: LTModifyUserProfileResponse) {
        logDebug("onIncomingModifyUserProfile response: $modifyUserProfileResponse")
        ProfileInfoManager.cleanProfileInfoByID(modifyUserProfileResponse.senderID)
        FileUtil.getProfileFile("${modifyUserProfileResponse.senderID}.jpg", false).remove()
        EventBus.getDefault().post(UserProfileChangeEvent(toUserID, toUserID))
    }

    override fun onError(errorInfo: LTErrorInfo) {
        logDebug("onError e: $errorInfo")
    }
}