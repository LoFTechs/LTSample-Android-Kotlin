package com.loftechs.sample.model.data.message

import com.stfalcon.chatkit.commons.models.IUser

class User(
        private var _id: String,
        private var _name: String,
) : IUser {
    override fun getId(): String {
        return _id
    }

    override fun getName(): String {
        return _name
    }

    override fun getAvatar(): String? {
        return null
    }
}