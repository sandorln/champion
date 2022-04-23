package com.sandorln.champion.database.shareddao

import com.sandorln.champion.model.VersionLol.VersionCategory

interface VersionDao {
    fun insertVersionCategory(versionCategory: VersionCategory)
    fun getVersionCategory(): VersionCategory

    fun insertVersionList(versionList: List<String>)
    fun getVersionList(): List<String>

    fun getVersion(): String
    fun insertVersion(version: String)
}