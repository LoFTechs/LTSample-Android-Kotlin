package com.loftechs.sample.main

import androidx.fragment.app.Fragment
import com.loftechs.sample.call.list.CallListFragment
import com.loftechs.sample.chat.list.ChatListFragment

enum class MainItemType {
    CHAT {
        override fun getFragment(): Fragment {
            return ChatListFragment.newInstance()
        }

        override fun onFabClick(view: MainContract.View?) {
            view?.gotoCreateChannel()
        }
    },
    CALL {
        override fun getFragment(): Fragment {
            return CallListFragment.newInstance()
        }

        override fun onFabClick(view: MainContract.View?) {
            view?.gotoCreateCall()
        }
    };

    abstract fun getFragment(): Fragment
    abstract fun onFabClick(view: MainContract.View?)
}