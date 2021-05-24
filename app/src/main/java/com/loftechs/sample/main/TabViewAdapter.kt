package com.loftechs.sample.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class TabViewAdapter(
        activity: FragmentActivity,
        private val mPresenter: MainContract.Presenter<MainContract.View>?,
        val arguments: Bundle,
) : FragmentStateAdapter(activity) {
    companion object {
        private val TAG = TabViewAdapter::class.java.simpleName
    }

    override fun getItemCount(): Int {
        return mPresenter?.tabItemCount ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return mPresenter?.getTabItemType(position)?.ordinal ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val tabItemType = mPresenter?.getTabItemType(position) ?: MainItemType.CHAT
        Timber.tag(TAG).d("createFragment $tabItemType")
        val fragment = tabItemType.getFragment()
        fragment.arguments = arguments
        return fragment
    }
}