package com.loftechs.sample.chat.settings

data class ChatSettingsData(
        val itemType: ItemType,
        val title: String,
        var isOpen: Boolean,
)
