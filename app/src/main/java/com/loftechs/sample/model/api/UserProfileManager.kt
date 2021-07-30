package com.loftechs.sample.model.api

import android.net.Uri
import com.loftechs.sample.LTSDKManager.getIMManager
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.im.message.LTFileMessageStatus
import com.loftechs.sdk.im.users.*
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import io.reactivex.ObservableSource
import timber.log.Timber
import java.util.*

object UserProfileManager {
    private val TAG = UserProfileManager::class.java.simpleName

    /**
     * Get user profile include nickname and profile
     */
    fun getUserProfile(receiverID: String, userID: String): Observable<LTUserProfile> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.queryUserProfile(Utils.createTransId(),
                            Collections.singleton(userID), null, "")
                }
                .map {
                    it.result[0]
                }
                .doOnNext {
                    Timber.tag(TAG).i("$userID getUserProfile : $it")
                }
                .doOnError { throwable: Throwable ->
                    Timber.tag(TAG).e("$userID getUserProfile error : $throwable")
                }
    }

    /**
     * Set avatar
     */
    fun setUserAvatar(receiverID: String, uri: Uri?): Observable<LTUserProfileFileResponse> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.setUserAvatar(Utils.createTransId(), uri)
                }
                .filter {
                    it.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE // upload avatar status is Done
                }
                .doOnNext {
                    logDebug("setUserAvatar : $it")
                }
                .doOnError {
                    logError("setUserAvatar", it)
                }
    }

    /**
     * delete avatar
     */
    fun deleteUserAvatar(receiverID: String): Observable<LTUserProfileFileResponse> {
        return ProfileInfoManager.getProfileInfoByUserID(receiverID, receiverID)
                .flatMap{
                    getIMManager(receiverID)
                            .flatMap { imManager: LTIMManager ->
                                it.profileFileInfo?.let { it ->
                                    imManager.userHelper.deleteUserAvatar(Utils.createTransId(), it)
                                }
                            }
                }.filter {
                    it.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE // upload avatar status is Done
                }
                .doOnNext {
                    logDebug("setUserAvatar : $it")
                }
                .doOnError {
                    logError("setUserAvatar", it)
                }
    }

    /**
     * Set nickname
     */
    fun setUserNickname(receiverID: String, nickname: String): Observable<MutableMap<String, Any>> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.setUserNickname(Utils.createTransId(), nickname)
                }
                .map {
                    it.userProfile
                }
                .doOnNext {
                    Timber.tag(TAG).i("$receiverID setUserProfile : $it")
                }
                .doOnError { throwable: Throwable ->
                    Timber.tag(TAG).e("$receiverID setUserProfile error : $throwable")
                }
    }

    /**
     * Get notify
     */
    fun getDeviceNotify(receiverID: String): Observable<LTUserNotifyData> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.queryDeviceNotify(Utils.createTransId())
                }
                .map {
                    Timber.tag(TAG).i("$receiverID getDeviceNotify : $it")
                    it.notifyData
                }
                .doOnError { throwable: Throwable ->
                    Timber.tag(TAG).e("$receiverID getDeviceNotify doOnError $throwable")
                }
    }

    /**
     * Set notify
     */
    fun setNotifyMute(receiverID: String, muteAll: Boolean): Observable<Boolean> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.setUserDeviceMute(Utils.createTransId(), muteAll, null)
                }
                .map {
                    Timber.tag(TAG).i("$receiverID setNotifyMute : $it")
                    true
                }
                .doOnError { throwable: Throwable ->
                    Timber.tag(TAG).e("$receiverID setNotifyMute error : $throwable")
                }
    }

    /**
     * set Notify Preview
     */
    fun setNotifyPreview(receiverID: String, hidingSender: Boolean, hidingContent: Boolean): Observable<Boolean> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.userHelper.SetUserDeviceNotifyPreview(Utils.createTransId(), hidingSender, hidingContent)
                }
                .map {
                    Timber.tag(TAG).i("$receiverID setNotifyPreview : $it")
                    true
                }
                .doOnError { throwable: Throwable ->
                    Timber.tag(TAG).e("$receiverID setNotifyPreview error : $throwable")
                }
    }
}