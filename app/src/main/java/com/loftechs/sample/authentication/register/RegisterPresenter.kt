package com.loftechs.sample.authentication.register

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.authentication.AbstractAuthenticationPresenter
import com.loftechs.sample.common.IntentKey.EXTRA_ACCOUNT_ID
import com.loftechs.sample.common.IntentKey.EXTRA_RECEIVER_ID
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.api.AccountManager
import com.loftechs.sample.model.data.AccountEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegisterPresenter : AbstractAuthenticationPresenter(), RegisterContract.Presenter<RegisterContract.View> {

    private var mView: RegisterContract.View? = null

    private val mDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun initBundle(arguments: Bundle) {
    }

    override fun create() {
    }

    override fun resume() {
    }

    override fun pause() {
        mDisposable.clear()
    }

    override fun destroy() {
        if (!mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }

    override fun bindView(view: RegisterContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun register(account: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            mView?.showErrorDialog(R.string.register_password_not_consistent)
            return
        }

        val subscribe = AccountManager.register(account, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .flatMap { response ->
                    logDebug("register registerResponse : $response")
                    response.users?.let { userList ->
                        if (userList.isNotEmpty()) {
                            val user = userList[0]
                            val error = user.err
                            if (error.isNullOrEmpty()) {
                                init(AccountEntity(account, password, user.userID, user.uuid))
                                        .doOnNext {
                                            val bundle = Bundle()
                                            bundle.putString(EXTRA_RECEIVER_ID, user.userID)
                                            bundle.putString(EXTRA_ACCOUNT_ID, account)
                                            mView?.gotoMainActivity(bundle)
                                        }
                                        .map { true }
                                        .observeOn(AndroidSchedulers.mainThread())
                            } else {
                                mView?.showErrorDialog(parseErrorMessage(error))
                                Observable.just(false)
                            }
                        } else {
                            Observable.error(Throwable("register response has no user list"))
                        }
                    }
                }
                .subscribe({
                    mView?.dismissProgressDialog()
                }, { e ->
                    mView?.dismissProgressDialog()
                    logError("register", e)
                    e.printStackTrace()
                })
        mDisposable.add(subscribe)
    }
}