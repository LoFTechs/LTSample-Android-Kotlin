package com.loftechs.sample.call.create

import android.os.Bundle
import com.loftechs.sample.call.voice.VoiceCallFragment
import com.loftechs.sample.common.create.AbstractCreateFragment
import com.loftechs.sample.common.create.CreateContract

class CreateCallFragment : AbstractCreateFragment() {

    companion object {
        val newInstance
            get() = CreateCallFragment()
    }

    override fun getCreatePresenter(): CreateContract.Presenter<CreateContract.View> {
        return CreateCallPresenter()
    }

    override fun gotoOneToOnePage(bundle: Bundle) {
        changeFragment(VoiceCallFragment.newInstance(), bundle)
    }
}