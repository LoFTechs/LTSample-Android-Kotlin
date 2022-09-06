package com.loftechs.sample.extensions

import android.net.Uri
import android.os.Environment
import com.loftechs.sample.LTSDKManager
import com.loftechs.sample.model.api.UserProfileManager
import com.loftechs.sdk.im.message.LTFileMessageStatus
import com.loftechs.sdk.im.message.LTFileTransferResult
import com.loftechs.sdk.im.queries.LTQueryUserDataResponse
import com.loftechs.sdk.im.queries.LTQueryUserDeviceNotifyResponse
import com.loftechs.sdk.im.queries.LTQueryUserProfileResponse
import com.loftechs.sdk.im.users.*
import com.loftechs.sdk.listener.LTCallbackResultListener
import com.loftechs.sdk.listener.LTErrorInfo
import com.loftechs.sdk.storage.LTFileInfo
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable


fun UserProfileManager.setUserPushToken(
        receiverID: String
): Observable<LTUserPushTokenResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val pushToken = "xxxxxxx" // FirebaseMessaging get token
                    it.userHelper.setUserPushToken(
                            transID,
                            pushToken,
                            object : LTCallbackResultListener<LTUserPushTokenResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserPushTokenResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.SetUserDeviceNotifyPreview(
        receiverID: String
): Observable<LTUserDeviceNotifyPreviewResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val hidingSender = true
                    val hidingContent = true
                    it.userHelper.SetUserDeviceNotifyPreview(
                            transID,
                            hidingSender,
                            hidingContent,
                            object : LTCallbackResultListener<LTUserDeviceNotifyPreviewResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserDeviceNotifyPreviewResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.setDeviceNotifySound(
        receiverID: String
): Observable<LTUserDeviceNotifySoundResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val soundType = 1
                    val soundID = "xxxxx.mp3"
                    it.userHelper.setDeviceNotifySound(
                            transID,
                            soundType,
                            soundID,
                            object : LTCallbackResultListener<LTUserDeviceNotifySoundResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserDeviceNotifySoundResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.setUserDeviceMute(
        receiverID: String
): Observable<LTUserDeviceMuteResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val muteAll = true
                    val time: Long? = null
                    it.userHelper.setUserDeviceMute(
                            transID,
                            muteAll,
                            time,
                            object : LTCallbackResultListener<LTUserDeviceMuteResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserDeviceMuteResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.setUserNickname(
        receiverID: String
): Observable<LTSetUserProfileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val nickname = "Robin"
                    it.userHelper.setUserNickname(
                            transID,
                            nickname,
                            object : LTCallbackResultListener<LTSetUserProfileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTSetUserProfileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.setUserAvatar(
        receiverID: String
): Observable<LTUserProfileFileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val avatarUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/Download/image.jpg")
                    it.userHelper.setUserAvatar(
                            transID,
                            avatarUri,
                            object : LTCallbackResultListener<LTUserProfileFileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserProfileFileResponse) {
                                    // 傳檔狀態
                                    if (result.fileMessageStatus == LTFileMessageStatus.STATUS_FILE) {
                                        val result: LTFileTransferResult = result.fileTransferResults[0]
                                        val fileType = result.fileType
                                        val status = result.status
                                        val loadingBytes = result.loadingBytes
                                        val totalLength = result.totalLength
                                    } else if (result.fileMessageStatus === LTFileMessageStatus.STATUS_MESSAGE) {
                                        // get LTUserProfileFileResponse result (extends LTSetUserProfileResponse)
                                    }
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.deleteUserAvatar(
        receiverID: String
): Observable<LTUserProfileFileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val fileInfo = LTFileInfo()//setUserProfileResponse.getUserProfile().get("profileImageFileInfo")
                    it.userHelper.deleteUserAvatar(
                            transID,
                            fileInfo,
                            object : LTCallbackResultListener<LTUserProfileFileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTUserProfileFileResponse) {
                                    //  檔案刪除成功狀態
                                    if (result.fileMessageStatus == LTFileMessageStatus.STATUS_MESSAGE) {
                                        // get LTUserProfileFileResponse result (extends LTSetUserProfileResponse)
                                    }
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.queryDeviceNotify(
        receiverID: String
): Observable<LTQueryUserDeviceNotifyResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    it.userHelper.queryDeviceNotify(
                            transID,
                            object : LTCallbackResultListener<LTQueryUserDeviceNotifyResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryUserDeviceNotifyResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.queryUserProfile(
        receiverID: String
): Observable<LTQueryUserProfileResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val userIDs = arrayOf("userID").toSet()
                    val phoneNumbers = arrayOf("+886901234567").toSet()
                    val brandID = "yourBrand"
                    it.userHelper.queryUserProfile(
                            transID,
                            userIDs,
                            phoneNumbers,
                            brandID,
                            object : LTCallbackResultListener<LTQueryUserProfileResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryUserProfileResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.queryUserID(
        receiverID: String
): Observable<LTQueryUserDataResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val phoneNumbers = arrayOf("+886901234567").toSet()
                    val brandID = "yourBrand"
                    it.userHelper.queryUserID(
                            transID,
                            phoneNumbers,
                            brandID,
                            object : LTCallbackResultListener<LTQueryUserDataResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryUserDataResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

fun UserProfileManager.queryPhoneNumber(
        receiverID: String
): Observable<LTQueryUserDataResponse> {
    return LTSDKManager.getIMManager(receiverID)
            .flatMap {
                Observable.create { emmit ->
                    val transID = Utils.createTransId()
                    val userIDs = arrayOf("userID").toSet()
                    it.userHelper.queryPhoneNumber(
                            transID,
                            userIDs,
                            object : LTCallbackResultListener<LTQueryUserDataResponse> {
                                override fun onError(errorInfo: LTErrorInfo) {
                                    emmit.onError(errorInfo)
                                }

                                override fun onResult(result: LTQueryUserDataResponse) {
                                    emmit.onNext(result)
                                    emmit.onComplete()
                                }
                            })
                }
            }
}

