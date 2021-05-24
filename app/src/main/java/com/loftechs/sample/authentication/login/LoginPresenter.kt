package com.loftechs.sample.authentication.login

import android.os.Bundle
import com.loftechs.sample.authentication.AbstractAuthenticationPresenter
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.logError
import com.loftechs.sample.model.api.AccountManager
import com.loftechs.sample.model.data.AccountEntity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginPresenter : AbstractAuthenticationPresenter(), LoginContract.Presenter<LoginContract.View> {

    private var mView: LoginContract.View? = null

    override fun initBundle(arguments: Bundle) {
    }

    override fun create() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
    }

    override fun bindView(view: LoginContract.View) {
        mView = view
    }

    override fun unbindView() {
        mView = null
    }

    override fun login(account: String, password: String) {
        AccountManager.login(account, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { response ->
                    logDebug("login loginResponse : $response")
                    if (response.returnCode == "0") {
                        init(AccountEntity(account, password, response.userID, response.uuid))
                                .doOnNext {
                                    val bundle = Bundle()
                                    bundle.putString(IntentKey.EXTRA_RECEIVER_ID, response.userID)
                                    bundle.putString(IntentKey.EXTRA_ACCOUNT_ID, account)
                                    mView?.gotoMainActivity(bundle)
                                }
                                .map { true }
                                .observeOn(AndroidSchedulers.mainThread())
                    } else {
                        response.returnCode?.let {
                            mView?.showErrorDialog(parseErrorMessage(it))
                        }
                        Observable.just(false)
                    }
                }
                .doOnSubscribe {
                    mView?.showProgressDialog()
                }
                .subscribe(object : Observer<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Boolean) {
                        mView?.dismissProgressDialog()
                    }

                    override fun onError(e: Throwable) {
                        mView?.dismissProgressDialog()
                        logError("login", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
    }
}