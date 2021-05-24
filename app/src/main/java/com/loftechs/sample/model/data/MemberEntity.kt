package com.loftechs.sample.model.data

import com.loftechs.sdk.im.channels.LTChannelRole

data class MemberEntity(
        var userID: String, var nickname: String, var number: String,
        var affID: LTChannelRole, var roID: LTChannelRole, var profileID: String,
)