package com.loftechs.sample.model.data.message

import com.loftechs.sdk.storage.LTFileInfo
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

data class ImageMessage(
        val messageID: String,
        val messageContent: String,
        val sender: User,
        val time: Date,
        private val filePath: String?,
        val fileInfo: LTFileInfo?,

) : BaseMessage(messageID, messageContent, sender, time), MessageContentType.Image {
    override fun getImageUrl(): String? {
        return filePath
    }
}