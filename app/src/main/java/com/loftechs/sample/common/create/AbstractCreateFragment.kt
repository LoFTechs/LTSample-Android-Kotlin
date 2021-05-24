package com.loftechs.sample.common.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.appbar.MaterialToolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.picker.PickerFragment
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.model.data.ProfileInfoEntity
import java.util.*

abstract class AbstractCreateFragment : AbstractFragment(), CreateContract.View {

    var mPresenter: CreateContract.Presenter<CreateContract.View>? = null
    private lateinit var mAdapter: BaseCreateAdapter
    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mToolbar: MaterialToolbar

    abstract fun getCreatePresenter(): CreateContract.Presenter<CreateContract.View>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_create, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: getCreatePresenter().apply {
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

        mAdapter = BaseCreateAdapter(mPresenter)
        mRecyclerView.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.base_create_recycler_view)
        mToolbar = view.findViewById(R.id.base_create_toolbar)
        mToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun refreshList(itemList: ArrayList<ProfileInfoEntity>) {
        logDebug("refreshList ++ $itemList")
        mAdapter.submitList(itemList)
    }

    override fun gotoPickerPage(userInfoList: ArrayList<ProfileInfoEntity>) {
        changeFragment(PickerFragment.newInstance(), requireArguments().apply {
            putSerializable(IntentKey.EXTRA_USER_INFO_LIST, userInfoList)
        })
    }

    override fun showCheckAccountDialog() {
        val customLayout: View = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText: EditText = customLayout.findViewById(R.id.editText)
        AlertDialog.Builder(requireContext())
                .setTitle(getText(R.string.string_account))
                .setView(customLayout)
                .setPositiveButton(R.string.common_confirm) { _, _ ->
                    if (editText.toString().isEmpty()) {
                        return@setPositiveButton
                    }
                    mPresenter?.checkUserExistAndCreate(editText.text.toString())
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }
}