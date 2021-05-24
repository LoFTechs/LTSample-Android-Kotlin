package com.loftechs.sample.model

import com.loftechs.sample.model.data.ProfileInfoEntity

object ProfileHelper {
    private val profileMap: MutableMap<String, ProfileInfoEntity> by lazy {
        PreferenceSetting.profileMap
    }

    fun isExist(id: String): Boolean {
        return profileMap.containsKey(id)
    }

    fun getProfileEntity(id: String): ProfileInfoEntity? {
        return profileMap[id]
    }

    fun setProfileEntity(profileEntity: ProfileInfoEntity) {
        profileMap[profileEntity.id] = profileEntity
        PreferenceSetting.profileMap = profileMap
    }

    fun cleanProfileEntity(id: String) {
        profileMap.remove(id)
        PreferenceSetting.profileMap = profileMap
    }

}



