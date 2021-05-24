package com.loftechs.sample.model

import com.loftechs.sample.model.data.AccountEntity

object AccountHelper {

    private val accountMap: MutableMap<String, AccountEntity> by lazy {
        PreferenceSetting.accountMap
    }

    fun setAccountEntity(accountEntity: AccountEntity) {
        accountMap[accountEntity.account] = accountEntity
        PreferenceSetting.accountMap = accountMap
    }

    val firstAccount: AccountEntity?
        get() {
            return if (accountMap.isEmpty()) {
                null
            } else accountMap.entries.iterator().next().value
        }

    fun hasExistAccount(): Boolean {
        return accountMap.isNotEmpty()
    }

    fun hasSelfNickname(): Boolean {
        return PreferenceSetting.nickname.isNotEmpty()
    }

    fun setSelfNickname(nickname: String) {
        PreferenceSetting.nickname = nickname
    }

    fun clearCache() {
        accountMap.clear()
    }
}