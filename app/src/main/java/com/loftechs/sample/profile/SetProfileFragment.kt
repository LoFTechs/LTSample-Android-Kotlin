package com.loftechs.sample.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loftechs.sample.R
import com.loftechs.sample.base.AbstractFragment
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.component.GlideApp
import com.loftechs.sample.extensions.REQUEST_SELECT_IMAGE
import com.loftechs.sample.extensions.logDebug
import com.loftechs.sample.extensions.pickImageFromExternal
import com.loftechs.sample.main.MainFragment
import com.loftechs.sample.utils.KeyboardUtil
import java.io.File

class SetProfileFragment : AbstractFragment(), SetProfileContract.View {

    private var mPresenter: SetProfileContract.Presenter<SetProfileContract.View>? = null

    private lateinit var mAvatar: ImageView
    private lateinit var mNicknameEditText: EditText
    private lateinit var mFab: FloatingActionButton

    private val mainFragment: MainFragment by lazy {
        MainFragment.newInstance()
    }

    companion object {
        fun newInstance(): SetProfileFragment {
            return SetProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_set_profile, container, false)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return mPresenter ?: SetProfilePresenter().apply {
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

    override fun gotoMainFragment() {
        changeFragmentWithoutBackStack(mainFragment, requireArguments())
    }

    override fun loadAvatar(file: File?) {
        GlideApp.with(mAvatar)
                .load(file)
                .placeholder(R.drawable.ic_add_photo_alpha)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mAvatar)

    }

    private fun initView(view: View) {
        mAvatar = view.findViewById(R.id.set_profile_avatar)
        mNicknameEditText = view.findViewById(R.id.set_profile_nickname)
        mFab = view.findViewById(R.id.set_profile_fab)

        mAvatar.setOnClickListener {
            if (mPresenter?.hasAvatar() == true) {
                val itemArray = arrayOf(
                        requireContext().resources.getString(R.string.set_profile_avatar_change),
                        requireContext().resources.getString(R.string.set_profile_avatar_delete)
                )

                AlertDialog.Builder(requireContext())
                        .setItems(itemArray) { dialogInterface, position ->
                            dialogInterface.dismiss()
                            if (position == 0) {
                                pickImageFromExternal()
                            } else if (position == 1) {
                               mPresenter?.deleteAvatar()
                            }
                        }
                        .show()
            } else {
                pickImageFromExternal()
            }
        }
        mFab.setOnClickListener {
            KeyboardUtil.hideKeyboard(requireActivity(), mNicknameEditText)
            mPresenter?.setNickname(mNicknameEditText.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        logDebug("onActivityResult $requestCode, $data")
        if (requestCode == REQUEST_SELECT_IMAGE) {
            mPresenter?.setProfileImage(data)
        }
    }

    override fun setNicknameText(nicknameText: String) {
        mNicknameEditText.setText(nicknameText)
    }

    override fun dismissFragment() {
        requireActivity().onBackPressed()
    }
}