package com.loftechs.sample.common.create

import android.os.Bundle
import com.loftechs.sample.BuildConfig
import com.loftechs.sample.R
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.ProfileInfoManager
import com.loftechs.sample.model.api.ChatFlowManager
import com.loftechs.sample.model.api.UserManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import com.loftechs.sdk.im.channels.LTChannelType
import com.loftechs.sdk.user.LTUserStatus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.ArrayList

abstract class AbstractCreatePresenter : CreateContract.Presenter<CreateContract.View> {

    private var mView: CreateContract.View? = null

    lateinit var mReceiverID: String
    lateinit var mArgument: Bundle

    abstract var itemList: ArrayList<ProfileInfoEntity>
    abstract val defaultItemList: ArrayList<ProfileInfoEntity>
    abstract val disposable: CompositeDisposable

    abstract fun getUserInfoByPosition(position: Int): ProfileInfoEntity
    abstract fun createOneToOne(userID: String, nickname: String): Observable<Bundle>

    override fun create() {
    }

    override fun resume() {
        mView?.refreshList(itemList)
        queryData()
    }

    override fun pause() {
        disposable.clear()
    }

    override fun destroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun bindView(view: CreateContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun initBundle(arguments: Bundle) {
        mArgument = arguments
        mReceiverID = arguments.getString(IntentKey.EXTRA_RECEIVER_ID, "")
    }

    override fun onBindViewHolder(view: BaseCreateAdapter.IContactItemView, position: Int) {
        val userInfo = getUserInfoByPosition(position)
        getItemType(position).bindData(view, this, userInfo)
    }

    private fun queryData() {
        val subscribe = ChatFlowManager.queryChannelListByChannelType(
                mReceiverID, Collections.singletonList(LTChannelType.SINGLE))
                .flatMapIterable { it }
                .flatMap {
                    val userID = ChatFlowManager.getUserIDFromOneToOneChannel(mReceiverID, it.chID)
                    ProfileInfoManager.getProfileInfoByUserID(mReceiverID, userID)
                }
                .collect<ArrayList<ProfileInfoEntity>>({ ArrayList() }, { list, data -> list.add(data) })
                .subscribe({
                    logDebug("queryData ++ onSuccess: $it")
                    itemList = ArrayList()
                    itemList.addAll(it)
                    mView?.refreshList(itemList)
                }, {
                    logError("queryData", it)
                    it.printStackTrace()
                })
        disposable.add(subscribe)
    }

    override fun operateClick(position: Int) {
        getItemType(position).doActionClick(mView, this, position)
    }

    override fun checkUserExistAndCreate(account: String) {
        val subscribe = UserManager.getUserStatusWithSemiUIDs(listOf(account))
                .map {
                    var user: LTUserStatus = it[0]
                    it.forEach { userStatus: LTUserStatus ->
                        if (BuildConfig.Brand_ID == userStatus.brandID) {
                            user = userStatus
                        }
                    }
                    user
                }
                .flatMap {
                    if (it.userID.isNullOrEmpty()) {
                        Observable.error(Throwable("no user status, response: $it"))
                    } else {
                        ProfileInfoManager.getProfileInfoByUserID(mReceiverID, it.userID)
                    }
                }
                .flatMap {
                    createOneToOne(it.id, it.displayName)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe({
                    logDebug("checkUserToCreateChannel onNext ++ $it")
                    mView?.dismissProgressDialog()
                    mView?.gotoOneToOnePage(it)
                }, {
                    logError("checkUserToCreateChannel", it)
                    it.printStackTrace()
                    mView?.dismissProgressDialog()
                    mView?.showErrorDialog(R.string.create_user_is_not_member)
                })
        disposable.add(subscribe)
    }
}