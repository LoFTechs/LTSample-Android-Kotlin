package com.loftechs.sample.model.api

import com.loftechs.sample.LTSDKManager.getIMManager
import com.loftechs.sdk.im.channels.*
import com.loftechs.sdk.im.queries.LTQueryChannelsResponse
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import timber.log.Timber

object ChatSettingsManager {

    private val TAG = ChatSettingsManager::class.java.simpleName

    /**
     * get channel information
     */
    fun getChannelInfo(userID: String, channelID: String): Observable<LTChannelResponse> {
        return getIMManager(userID)
                .flatMap {
                    it.channelHelper.queryChannel(Utils.createTransId(), channelID, false)
                }
                .filter { response: LTQueryChannelsResponse ->
                    response.channels != null && response.channels.isNotEmpty()
                }
                .map { queryChannelsResponse: LTQueryChannelsResponse ->
                    queryChannelsResponse.channels[0]
                }
                .doOnError {
                    Timber.tag(TAG).e("$userID getChannelInfo error : $it")
                }
    }

    /**
     * Set channel subject
     */
    fun setChannelSubject(userID: String, channelID: String, subject: String): Observable<LTChannelProfileResponse> {
        return getIMManager(userID)
                .flatMap {
                    it.channelHelper.setChannelSubject(Utils.createTransId(), channelID, subject)
                }
                .map { setChannelSubjectResponse: LTChannelProfileResponse ->
                    setChannelSubjectResponse
                }
                .doOnError {
                    Timber.tag(TAG).e("$userID setChannelSubject error : $it")
                }
    }

    /**
     * Set channel mute status
     */
    fun setChannelMute(userID: String, channelID: String, mute: Boolean): Observable<LTChannelPreferenceResponse> {
        return getIMManager(userID)
                .concatMap {
                    it.channelHelper.setChannelMute(Utils.createTransId(), channelID, mute)
                }
                .doOnError {
                    Timber.tag(TAG).e("$userID setChannelMute doOnError $it")
                }
    }

    fun dismissChannel(userID: String, channelID: String): Observable<LTDismissChannelResponse> {
        return getIMManager(userID)
                .concatMap {
                    it.channelHelper.dismissChannel(Utils.createTransId(), channelID)
                }
                .doOnError {
                    Timber.tag(TAG).e("$userID dismissChannel doOnError: $it")
                }
    }

    fun leaveChannel(userID: String, channelID: String): Observable<LTLeaveChannelResponse> {
        return getIMManager(userID)
                .concatMap {
                    it.channelHelper.leaveChannel(Utils.createTransId(), channelID)
                }
                .doOnError {
                    Timber.tag(TAG).e("$userID leaveChannel doOnError $it")
                }
    }
}