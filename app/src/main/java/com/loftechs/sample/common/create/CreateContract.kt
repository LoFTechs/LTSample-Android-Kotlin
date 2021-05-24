package com.loftechs.sample.common.create

import android.os.Bundle
import com.loftechs.sample.base.BaseContract
import com.loftechs.sample.chat.create.CreateItemType
import com.loftechs.sample.model.data.ProfileInfoEntity
import java.util.*

interface CreateContract {
    interface View : BaseContract.BaseView {
        fun refreshList(itemList: ArrayList<ProfileInfoEntity>)
        fun gotoPickerPage(userInfoList: ArrayList<ProfileInfoEntity>)
        fun showCheckAccountDialog()
        fun gotoOneToOnePage(bundle: Bundle)
    }

    interface Presenter<T> : BaseContract.Presenter<T> {
        fun getItemType(position: Int): CreateItemType
        fun onBindViewHolder(view: BaseCreateAdapter.IContactItemView, position: Int)
        fun operateClick(position: Int)
        fun checkUserExistAndCreate(account: String)
    }
}