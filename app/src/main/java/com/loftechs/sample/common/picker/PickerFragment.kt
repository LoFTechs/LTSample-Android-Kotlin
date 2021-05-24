package com.loftechs.sample.common.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loftechs.sample.R
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.chat.create.group.CreateGroupFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.model.data.ProfileInfoEntity
import java.util.*

class PickerFragment : AbstractFragment(), PickerContract.View {

    private var mPresenter: PickerContract.Presenter<PickerContract.View>? = null
    private lateinit var mAdapter: PickerAdapter
    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mFab: FloatingActionButton

    companion object {
        fun newInstance(): PickerFragment {
            return PickerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: PickerPresenter().apply {
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
        mAdapter = PickerAdapter(mPresenter)
        mRecyclerView.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.picker_recycler_view)
        mToolbar = view.findViewById(R.id.picker_toolbar)
        mToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        mFab = view.findViewById(R.id.picker_fab)
        mFab.setOnClickListener {
            mPresenter?.checkCreateGroup()
        }
    }

    override fun refreshList(itemList: ArrayList<ProfileInfoEntity>) {
        mAdapter.submitList(itemList)
    }

    override fun gotoCreateGroupPage(selectedList: ArrayList<ProfileInfoEntity>) {
        changeFragment(CreateGroupFragment.newInstance(), requireArguments().apply {
            putSerializable(IntentKey.EXTRA_SELECTED_LIST, selectedList)
        })
    }
}
