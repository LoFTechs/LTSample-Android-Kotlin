package com.loftechs.sample.chat.create

import android.os.Bundle
import com.loftechs.sample.chat.chatroom.ChatFragment
import com.loftechs.sample.common.create.AbstractCreateFragment
import com.loftechs.sample.common.create.CreateContract

class CreateChannelFragment : AbstractCreateFragment() {

    companion object {
        fun newInstance(): CreateChannelFragment {
            return CreateChannelFragment()
        }
    }

    override fun getCreatePresenter(): CreateContract.Presenter<CreateContract.View> {
        return CreateChannelPresenter()
    }

    override fun gotoOneToOnePage(bundle: Bundle) {
        changeFragmentWithoutBackStack(ChatFragment.newInstance, bundle)
    }
}
