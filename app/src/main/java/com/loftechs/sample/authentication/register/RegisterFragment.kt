package com.loftechs.sample.authentication.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.extensions.REQUEST_INTRO

class RegisterFragment : AbstractFragment(), RegisterContract.View {

    private var mPresenter: RegisterContract.Presenter<RegisterContract.View>? = null

    private lateinit var mAccountEditText: EditText
    private lateinit var mPasswordEditText: EditText
    private lateinit var mConfirmPasswordEditText: EditText
    private lateinit var mLinkText: TextView
    private lateinit var mFab: FloatingActionButton

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: RegisterPresenter().apply {
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

    private fun initView(root: View) {
        mAccountEditText = root.findViewById(R.id.register_account)
        mPasswordEditText = root.findViewById(R.id.register_password)
        mConfirmPasswordEditText = root.findViewById(R.id.register_confirm_password)
        mLinkText = root.findViewById(R.id.register_link)
        mFab = root.findViewById(R.id.register_fab)

        mLinkText.setOnClickListener {
            gotoLoginPage()
        }
        mFab.setOnClickListener {
            mPresenter?.register(mAccountEditText.text.toString(), mPasswordEditText.text.toString(),
                    mConfirmPasswordEditText.text.toString())
        }
    }

    private fun gotoLoginPage() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun gotoMainActivity(bundle: Bundle) {
        activity?.let {
            val intent = Intent()
            intent.putExtras(bundle)
            it.setResult(REQUEST_INTRO, intent)
            it.finishAfterTransition()
        }
    }
}