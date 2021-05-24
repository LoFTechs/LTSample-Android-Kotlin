package com.loftechs.sample.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.call.create.CreateCallFragment
import com.loftechs.sample.chat.create.CreateChannelFragment
import com.loftechs.sample.common.picker.PickerFragment
import com.loftechs.sample.profile.SettingFragment

class MainFragment : AbstractFragment(), MainContract.View {

    private var mPresenter: MainContract.Presenter<MainContract.View>? = null

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager2: ViewPager2
    private lateinit var mFab: FloatingActionButton

    private val mTabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            mPresenter?.getFabIconResource(tab.position)?.let {
                mFab.setImageResource(it)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    }

    private val createChannelFragment: CreateChannelFragment by lazy {
        CreateChannelFragment.newInstance()
    }

    private val createCallFragment: CreateCallFragment by lazy {
        CreateCallFragment.newInstance
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: MainPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
    }

    private fun initView(view: View) {
        initToolbar(view)
        initFab(view)
        initTabLayout(view)
        initViewPager(view)
    }

    private fun initToolbar(view: View) {
        mToolbar = view.findViewById(R.id.main_toolbar)
        mToolbar.navigationIcon = null
        mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.new_group -> {
                    changeFragment(PickerFragment.newInstance(), requireArguments())
                    true
                }
                R.id.settings -> {
                    changeFragment(SettingFragment.newInstance, requireArguments())
                    true
                }
                R.id.log_out -> {
                    showLogOutDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun initTabLayout(view: View) {
        mTabLayout = view.findViewById(R.id.main_tab_layout)
        mTabLayout.addOnTabSelectedListener(mTabListener)
    }

    private fun initViewPager(view: View) {
        mViewPager2 = view.findViewById(R.id.main_view_pager)
        mViewPager2.adapter = TabViewAdapter(requireActivity(), mPresenter, requireArguments())
        TabLayoutMediator(mTabLayout, mViewPager2) { tab, position ->
            mPresenter?.getTabStringResourceID(position)?.let {
                tab.text = getString(it)
            }
        }.attach()
    }

    private fun initFab(view: View) {
        mFab = view.findViewById(R.id.main_fab)
        mFab.setOnClickListener {
            mPresenter?.onFabClick(mViewPager2.currentItem)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
        mTabLayout.removeOnTabSelectedListener(mTabListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mViewPager2.isInitialized) {
            mViewPager2.adapter = null
        }
    }

    private fun showLogOutDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.main_tab_menu_item_log_out)
                .setPositiveButton(R.string.common_confirm) { _, _ ->
                    mPresenter?.logout()
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }

    override fun gotoCreateChannel() {
        changeFragment(createChannelFragment, arguments)
    }

    override fun gotoCreateCall() {
        changeFragment(createCallFragment, arguments)
    }
}