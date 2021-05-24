package com.loftechs.sample.chat.create

import com.loftechs.sample.R
import com.loftechs.sample.common.create.AbstractCreatePresenter
import com.loftechs.sample.common.create.BaseCreateAdapter
import com.loftechs.sample.common.create.CreateContract
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.AvatarManager
import com.loftechs.sample.model.data.ProfileInfoEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

enum class CreateItemType(val title: String) {

    GROUP("New Group") {
        override fun bindData(view: BaseCreateAdapter.IContactItemView, presenter: AbstractCreatePresenter, userInfo: ProfileInfoEntity) {
            view.setTitleText(this.title)
            view.bindIcon(R.drawable.ic_add_new_group)
        }

        override fun doActionClick(
                view: CreateContract.View?,
                presenter: AbstractCreatePresenter,
                position: Int,
        ) {
            val arrayList = ArrayList(presenter.itemList)
            arrayList.removeAll(presenter.defaultItemList)
            view?.gotoPickerPage(arrayList)
        }
    },
    NEW_CONTACT("New Contact") {
        override fun bindData(view: BaseCreateAdapter.IContactItemView, presenter: AbstractCreatePresenter, userInfo: ProfileInfoEntity) {
            view.setTitleText(this.title)
            view.bindIcon(R.drawable.ic_action_add_person)
        }

        override fun doActionClick(
                view: CreateContract.View?,
                presenter: AbstractCreatePresenter,
                position: Int,
        ) {
            view?.showCheckAccountDialog()
        }
    },
    ITEM("Unknown") {
        override fun bindData(view: BaseCreateAdapter.IContactItemView, presenter: AbstractCreatePresenter, userInfo: ProfileInfoEntity) {
            view.setTitleText(userInfo.displayName)
            val subscribe = AvatarManager.loadAvatar(presenter.mReceiverID, userInfo.profileFileInfo)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                view.bindAvatar(it)
                            },
                            {
                                logError("bindData", it)
                                view.bindAvatar(null)
                            }
                    )
            presenter.disposable.add(subscribe)
        }

        override fun doActionClick(
                view: CreateContract.View?,
                presenter: AbstractCreatePresenter,
                position: Int,
        ) {
            val userInfo = presenter.itemList[position]
            val subscribe = presenter.createOneToOne(userInfo.id, userInfo.displayName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        view?.showProgressDialog()
                    }
                    .subscribe({
                        logDebug("createOneToOne onNext ++ $it")
                        view?.dismissProgressDialog()
                        view?.gotoOneToOnePage(it)
                    }, {
                        logError("createOneToOne", it)
                        it.printStackTrace()
                        view?.dismissProgressDialog()
                    })
            presenter.disposable.add(subscribe)
        }
    };

    abstract fun bindData(view: BaseCreateAdapter.IContactItemView, presenter: AbstractCreatePresenter, userInfo: ProfileInfoEntity)
    abstract fun doActionClick(
            view: CreateContract.View?,
            presenter: AbstractCreatePresenter, position: Int,
    )

    fun getUserInfo(): ProfileInfoEntity {
        return ProfileInfoEntity("", title, "", null, 0L)
    }
}