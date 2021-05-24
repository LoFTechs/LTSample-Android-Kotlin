package com.loftechs.sample.model.data.message

import com.stfalcon.chatkit.commons.models.IMessage
import java.util.*

open class BaseMessage(
        private var _id: String,
        private var _text: String,
        private var _user: User,
        private var _date: Date,
) : IMessage {

    override fun getId(): String {
        return _id
    }

    override fun getText(): String {
        return _text
    }

    override fun getUser(): User {
        return _user
    }

    override fun getCreatedAt(): Date {
        return _date
    }
}