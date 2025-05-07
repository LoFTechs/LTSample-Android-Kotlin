package com.loftechs.sample.model

import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.UserProfileManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object ProfileInfoManager {
    private const val EXPIRE_DAY = 1L

    fun getProfileInfoByUserID(
        receiverID: String,
        userID: String,
        number: String
    ): Observable<ProfileInfoEntity> {
        return Observable.just(
            getLocalProfile(userID) ?: ProfileInfoEntity(
                userID,
                number,
                "",
                null,
                System.currentTimeMillis()
            )
        )
    }

    fun getProfileInfoByUserID(receiverID: String, userID: String): Observable<ProfileInfoEntity> {
        return getLocalProfile(userID)?.let {
            Observable.just(it)
        } ?: run {
            requestRemoteByUserID(receiverID, userID)
        }
    }

    fun getProfileInfoByChatID(receiverID: String, chatID: String): Observable<ProfileInfoEntity> {
        return getLocalProfile(chatID)?.let {
            Observable.just(it)
        } ?: run {
            requestRemoteByChatID(receiverID, chatID)
        }
    }

    fun cleanProfileInfoByID(id: String) {
        ProfileHelper.cleanProfileEntity(id)
    }

    private fun getLocalProfile(userIDorChatID: String): ProfileInfoEntity? {
        val profileInfoEntity = ProfileHelper.getProfileEntity(userIDorChatID)
        return (profileInfoEntity?.let {
            if (!isExpire(it.updateTime)) {
                logDebug("[local]getLocalProfile $userIDorChatID : $it")
                it
            } else {
                null
            }
        } ?: run {
            null
        })
    }

    private fun requestRemoteByUserID(
        receiverID: String,
        userID: String
    ): Observable<ProfileInfoEntity> {
        logDebug("[remote]requestRemoteByUserID $userID")
        return UserProfileManager.getUserProfile(receiverID, userID)
            .map {
                logDebug("[remote]getProfileInfoByUser  $userID,response $it")
                ProfileInfoEntity(
                    it.userID, it.nickname,
                    it.profileImageID, it.profileImageFileInfo, System.currentTimeMillis()
                )
            }
            .doOnNext {
                updateProfileInfo(it)
            }
    }

    private fun requestRemoteByChatID(
        receiverID: String,
        chatID: String
    ): Observable<ProfileInfoEntity> {
        logDebug("[remote]requestRemoteByChatID $chatID")
        return ChatFlowManager.queryChannelByID(receiverID, chatID)
            .map {
                logDebug("[remote]getProfileInfoByChat  $chatID,response $it")
                ProfileInfoEntity(
                    chatID, it.subject,
                    it.profileImageID, it.profileImageFileInfo, System.currentTimeMillis()
                )
            }
            .doOnNext {
                updateProfileInfo(it)
            }
    }

    fun updateProfileInfo(profileInfoEntity: ProfileInfoEntity) {
        return ProfileHelper.setProfileEntity(profileInfoEntity)
    }

    private fun isExpire(time: Long): Boolean {
        return System.currentTimeMillis() > (TimeUnit.DAYS.toMillis(EXPIRE_DAY) + time)
    }
}