package com.loftechs.sample.chat.member

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.SampleApp
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.ChatEvent
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.MemberManager
import com.loftechs.sample.model.api.UserManager
import com.loftechs.sample.model.data.MemberEntity
import com.loftechs.sdk.im.channels.LTChannelRole
import com.loftechs.sdk.im.message.LTMemberModel
import com.loftechs.sdk.storage.LTFileInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import kotlin.collections.HashSet

class MemberListPresenter : MemberListContract.Presenter<MemberListContract.View> {

    private var mView: MemberListContract.View? = null

    private var mIsSelfAdmin: Boolean = false
    private var mReceiverID: String = ""
    private var mChID: String = ""
    private var mSubject: String = ""

    private val mMemberEntities: ArrayList<MemberEntity> by lazy {
        ArrayList()
    }

    companion object {
        val TAG = MemberListPresenter::class.java.simpleName
    }

    private val mDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
        mChID = arguments.getString(IntentKey.EXTRA_CHANNEL_ID, "")
        mSubject = arguments.getString(IntentKey.EXTRA_CHANNEL_SUBJECT, "")
    }

    override fun create() {
    }

    override fun resume() {
        refreshData(mChID)
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: MemberListContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    private fun requestData() {
        Timber.tag(TAG).d("$mReceiverID chID  $mChID")
        val subscribe = MemberManager.queryAllChannelMembers(mReceiverID, mChID)
                .map {
                    mMemberEntities.addAll(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    mView?.dismissProgressDialog()
                    mMemberEntities.sortByDescending { it.roID }
                    mView?.refreshList(ArrayList(mMemberEntities))
                    parseAdmin(mMemberEntities).subscribe()
                    val chatEvent = ChatEvent(mReceiverID, mChID)
                    chatEvent.memberCount = mMemberEntities.size
                    EventBus.getDefault().post(chatEvent)
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.member_list_get_member_list_error)
                })
        mDisposable.add(subscribe)
    }

    override fun onBindViewHolder(view: MemberListAdapter.IItemView, position: Int) {
        val member = mMemberEntities[position]
        view.enableDeleteButton(!isSelf(member.userID) && isSelfAdmin())
        val subscribe = ProfileInfoManager.getProfileInfoByUserID(mReceiverID, member.userID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var name = it.displayName
                    if (isSelf(it.id)) {
                        name = SampleApp.context.getString(R.string.string_you)
                    }
                    if (member.roID > LTChannelRole.PARTICIPANT) {
                        name += " " + SampleApp.context.getString(R.string.member_list_admin)
                    }
                    view.setDisplayName(name)
                    bindAvatar(view, it.profileFileInfo)
                }, {
                    logError("onBindViewHolder", it)
                })
        mDisposable.add(subscribe)
    }

    private fun bindAvatar(view: MemberListAdapter.IItemView, fileInfo: LTFileInfo?) {
        val subscribe = AvatarManager.loadAvatar(mReceiverID, fileInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.bindAvatar(it)
                }, {
                    logError("bindAvatar", it)
                    view.bindAvatar(null)
                })
        mDisposable.add(subscribe)
    }

    override fun requestRemoveMember(id: String, displayName: String) {
        mView?.showRemoveMemberDialog(mSubject, displayName, id)
    }

    override fun inviteMember(accountID: String) {
        val subscribe = UserManager.getUserStatusWithSemiUIDs(Collections.singletonList(accountID))
                .flatMap {
                    val memberModels = HashSet<LTMemberModel>()
                    for (user in it) {
                        val memberModel = LTMemberModel(user.userID)
                        memberModel.chNickname = user.semiUID
                        memberModels.add(memberModel)
                    }
                    MemberManager.inviteMember(mReceiverID, mChID, memberModels)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    mView?.dismissProgressDialog()
                    refreshData(mChID)
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.member_list_invite_member_error)
                })
        mDisposable.add(subscribe)
    }

    override fun kickMember(id: String) {
        val subscribe = MemberManager.kickMembers(mReceiverID, mChID, setOf(id))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    mView?.dismissProgressDialog()
                    refreshData(mChID)
                }, {
                    mView?.dismissProgressDialog()
                    mView?.showSnackBar(R.string.member_list_kick_member_error)
                })
        mDisposable.add(subscribe)
    }

    private fun isSelf(userID: String): Boolean {
        return userID == mReceiverID
    }

    private fun isSelfAdmin(): Boolean {
        return mIsSelfAdmin
    }

    private fun parseAdmin(members: ArrayList<MemberEntity>): Observable<Boolean> {
        return Observable.fromIterable(members)
                .filter {
                    isSelf(it.userID)
                }
                .map {
                    if (it.roID > LTChannelRole.PARTICIPANT) {
                        mIsSelfAdmin = true
                    }
                    mIsSelfAdmin
                }
    }

    override fun refreshData(chID: String) {
        if (chID != mChID) {
            return
        }
        mMemberEntities.clear()
        requestData()
    }
}
