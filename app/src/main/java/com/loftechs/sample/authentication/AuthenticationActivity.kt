package com.loftechs.sample.authentication

import android.os.Bundle
import com.loftechs.sample.R
import com.loftechs.sample.authentication.login.LoginFragment
import com.loftechs.sample.base.BaseActivity

class AuthenticationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            initFragment(LoginFragment.newInstance(), null)
        }
    }
}