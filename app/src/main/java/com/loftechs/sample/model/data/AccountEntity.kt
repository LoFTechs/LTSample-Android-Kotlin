package com.loftechs.sample.model.data

import java.io.Serializable

data class AccountEntity(
        var account: String,
        var password: String,
        var userID: String,
        var uuid: String
) : Serializable