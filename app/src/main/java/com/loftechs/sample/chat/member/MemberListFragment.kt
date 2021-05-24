package com.loftechs.sample.chat.member

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.loftechs.sample.R
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.common.event.MemberChangedEvent
import com.loftechs.sample.component.CustomLinearLayoutManager
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.model.data.MemberEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MemberListFragment : AbstractFragment(), MemberListContract.View {

    private var mPresenter: MemberListContract.Presenter<MemberListContract.View>? = null

    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mMemberListAdapter: MemberListAdapter

    companion object {
        fun newInstance(): MemberListFragment {
            return MemberListFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_member_list, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: MemberListPresenter().apply {
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
        mMemberListAdapter = MemberListAdapter(mPresenter)
        mRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mMemberListAdapter
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    override fun refreshList(members: List<MemberEntity>) {
        mToolbar.title = getString(R.string.member_list_title, members.size.toString())
        mMemberListAdapter.submitList(members)
    }

    private fun initView(view: View) {
        mRecyclerView = view.findViewById(R.id.member_list_recycler_view)
        mAppBarLayout = view.findViewById(R.id.bar_layout)
        mToolbar = view.findViewById(R.id.top_bar)
        mToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        mToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.invite -> {
                    showInviteDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showInviteDialog() {
        val customLayout: View = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText: EditText = customLayout.findViewById(R.id.editText)
        AlertDialog.Builder(requireContext())
                .setTitle(getText(R.string.string_account))
                .setView(customLayout)
                .setPositiveButton(R.string.common_confirm) { _, _ ->
                    if (editText.toString().isEmpty()) {
                        return@setPositiveButton
                    }
                    mPresenter?.inviteMember(editText.text.toString())
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: MemberChangedEvent) {
        mPresenter?.refreshData(event.chID)
    }

    override fun showRemoveMemberDialog(channelName: String, displayName: String, id: String) {
        AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.member_list_kick_member_message, displayName, channelName))
                .setPositiveButton(R.string.common_confirm) { _, _ ->
                    mPresenter?.kickMember(id)
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
    }
}