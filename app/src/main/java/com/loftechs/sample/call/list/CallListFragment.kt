package com.loftechs.sample.call.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.loftechs.sample.R
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.call.voice.VoiceCallFragment
import com.loftechs.sample.common.IntentKey
import com.loftechs.sample.common.event.CallCDREvent
import com.loftechs.sample.component.CustomLinearLayoutManager
import com.loftechs.sample.component.VerticalRecyclerView
import com.loftechs.sample.model.api.CallManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.ArrayList

class CallListFragment : AbstractFragment(), CallListContract.View {

    private var mPresenter: CallListContract.Presenter<CallListContract.View>? = null

    private lateinit var mRecyclerView: VerticalRecyclerView
    private lateinit var mAdapter: CallListAdapter

    companion object {
        private val TAG = CallListFragment::class.java.simpleName
        fun newInstance(): CallListFragment {
            return CallListFragment()
        }
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: CallListPresenter().apply {
            mPresenter = this
        }
    }

    override fun clearPresenter() {
        mPresenter = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_call_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.bindView(this)
        initView(view)
        mAdapter = CallListAdapter(mPresenter)
        mRecyclerView.layoutManager =
            CustomLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mAdapter
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.unbindView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initView(root: View) {
        mRecyclerView = root.findViewById(R.id.list_view)
    }

    override fun gotoVoiceCall(receiverID: String, callUserID: String) {
        changeFragment(VoiceCallFragment.newInstance(), requireArguments().apply {
            putString(IntentKey.EXTRA_CALL_USER_ID, callUserID)
            putInt(IntentKey.EXTRA_CALL_STATE_TYPE, CallState.OUT.ordinal)
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CallCDREvent) {
        if (event.receiverID == mPresenter?.getReceiverID()) {
            addData(event.callLogData)
        }
    }

    override fun addData(data: Any) {
        var callLogList: List<CallLogData> = mAdapter.currentList
        callLogList.toMutableList().let {
            if (data is CallLogData) {
                it.add(0, data)
            } else {
                it += data as MutableList<CallLogData>
            }
            callLogList = it
        }
        Timber.tag(TAG).d("adapter count : ${callLogList.size}")
        mAdapter.submitList(callLogList)
    }
}


