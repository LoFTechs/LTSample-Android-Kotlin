package com.loftechs.sample.model.data

import com.loftechs.sdk.storage.LTFileInfo

data class ProfileInfoEntity(
        /**
         * id : userID or ChatID
         */
        val id: String,
        var displayName: String,
        var profileImage: String,
        var profileFileInfo: LTFileInfo?,
        var updateTime: Long,
)
