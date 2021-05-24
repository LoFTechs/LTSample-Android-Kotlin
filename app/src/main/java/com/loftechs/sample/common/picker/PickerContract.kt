package com.loftechs.sample.common.picker

import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.model.data.ProfileInfoEntity
import java.util.*

interface PickerContract {
    interface View : BaseContract.BaseView {
        fun refreshList(itemList: ArrayList<ProfileInfoEntity>)
        fun gotoCreateGroupPage(selectedList: ArrayList<ProfileInfoEntity>)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun onBindViewHolder(view: PickerAdapter.IPickerItemView, position: Int)
        fun checkCreateGroup()
        fun operateCheck(position: Int, isChecked: Boolean)
    }
}