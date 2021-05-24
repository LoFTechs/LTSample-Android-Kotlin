package com.loftechs.sample.model.api

import com.loftechs.sample.LTSDKManager.getIMManager
import com.loftechs.sample.model.data.MemberEntity
import com.loftechs.sdk.im.LTIMManager
import com.loftechs.sdk.im.channels.LTInviteMemberResponse
import com.loftechs.sdk.im.channels.LTJoinMethod
import com.loftechs.sdk.im.channels.LTKickMemberResponse
import com.loftechs.sdk.im.channels.LTMemberPrivilege
import com.loftechs.sdk.im.message.LTMemberModel
import com.loftechs.sdk.im.queries.LTQueryChannelMembersResponse
import com.loftechs.sdk.utils.Utils
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.HashSet

object MemberManager {

    private val TAG = MemberManager::class.java.simpleName
    private const val BATCH_COUNT = 20

    /**
     * Loftechs SDK LTChannelHelper queryChannelMembersByChID
     */
    private fun queryChannelMembersByChID(receiverID: String, chID: String, lastUserID: String): Observable<LTQueryChannelMembersResponse> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.channelHelper.queryChannelMembersByChID(Utils.createTransId(), chID, lastUserID, BATCH_COUNT)
                }
    }

    /**
     * query all members by batch
     */
    fun queryAllChannelMembers(receiverID: String, chID: String): Observable<ArrayList<MemberEntity>> {
        val lastUserID = arrayOf<String?>("")
        return Observable
                .defer { queryChannelMembersByChID(receiverID, chID, lastUserID[0] ?: "") }
                .filter { it.members != null && it.members.isNotEmpty() }
                .flatMapIterable { it.members }
                .map { memberResponse: LTMemberPrivilege ->
                    MemberEntity(memberResponse.userID, memberResponse.nickname, memberResponse.phoneNumber,
                            memberResponse.affID, memberResponse.roleID, memberResponse.profileID)
                }
                .collect({ ArrayList() }, BiConsumer(ArrayList<MemberEntity>::add) as BiConsumer<ArrayList<MemberEntity>, MemberEntity>)
                .toObservable()
                .doOnNext(Consumer { channelMemberEntities -> // 若是最後一批，就把lastUserID 壓為 null
                    if (channelMemberEntities.isEmpty()) {
                        lastUserID[0] = null
                        return@Consumer
                    }
                    val lastMemberEntity = channelMemberEntities[channelMemberEntities.size - 1]
                    lastUserID[0] = if (channelMemberEntities.size == BATCH_COUNT) lastMemberEntity.userID else null
                })
                .repeatUntil { lastUserID[0] == null }
                .subscribeOn(Schedulers.newThread())
                .doOnError {
                    Timber.tag(TAG).e("$receiverID queryAllChannelMembers onError ++ $it")
                }
                .doOnComplete { Timber.tag(TAG).d("$receiverID queryAllChannelMembers onComplete") }
    }

    fun kickMembers(receiverID: String, chID: String, kickMembersUserIDs: Set<String>): Observable<LTKickMemberResponse> {
        return Observable.fromIterable(kickMembersUserIDs)
                .map {
                    LTMemberModel(it)
                }
                .collect({ HashSet() }, BiConsumer(HashSet<LTMemberModel>::add) as BiConsumer<HashSet<LTMemberModel>, LTMemberModel>)
                .toObservable()
                .concatMap { memberModels: HashSet<LTMemberModel> ->
                    getIMManager(receiverID)
                            .concatMap { imManager: LTIMManager ->
                                imManager.channelHelper.kickMembers(Utils.createTransId(), chID, memberModels)
                            }
                }
                .doOnError {
                    Timber.tag(TAG).e("$receiverID kickMembers onError ++ $it")
                }
    }

    fun inviteMember(receiverID: String, chID: String, memberModels: Set<LTMemberModel>): Observable<LTInviteMemberResponse> {
        return getIMManager(receiverID)
                .flatMap { imManager: LTIMManager ->
                    imManager.channelHelper.inviteMembers(Utils.createTransId(), chID, memberModels, LTJoinMethod.NORMAL)
                }
                .doOnError {
                    Timber.tag(TAG).e("$receiverID inviteMember onError ++ $it")
                }
    }
}