package com.loftechs.sample.authentication.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loftechs.sample.R
import com.loftechs.sample.authentication.register.RegisterFragment
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.extensions.REQUEST_INTRO

class LoginFragment : AbstractFragment(), LoginContract.View {

    private var mPresenter: LoginContract.Presenter<LoginContract.View>? = null

    private lateinit var mAccountEditText: EditText
    private lateinit var mPasswordEditText: EditText
    private lateinit var mLinkText: TextView
    private lateinit var mFab: FloatingActionButton

    private val registerFragment: RegisterFragment by lazy {
        RegisterFragment.newInstance()
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: LoginPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mAccountEditText = view.findViewById(R.id.login_account)
        mPasswordEditText = view.findViewById(R.id.login_password)
        mLinkText = view.findViewById(R.id.login_link)
        mFab = view.findViewById(R.id.login_fab)

        mLinkText.setOnClickListener {
            gotoRegisterPage()
        }
        mFab.setOnClickListener {
            mPresenter?.login(mAccountEditText.text.toString(), mPasswordEditText.text.toString())
        }
    }

    override fun gotoMainActivity(bundle: Bundle) {
        activity?.let {
            val intent = Intent()
            intent.putExtras(bundle)
            it.setResult(REQUEST_INTRO, intent)
            it.finishAfterTransition()
        }
    }

    private fun gotoRegisterPage() {
        activity?.supportFragmentManager?.popBackStackImmediate()
        changeFragment(registerFragment, null)
    }
}