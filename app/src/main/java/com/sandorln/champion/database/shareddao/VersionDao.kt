package com.sandorln.champion.database.shareddao

import com.sandorln.champion.model.VersionLol.VersionCategory

interface VersionDao {
    fun insertVersionCategory(versionCategory: VersionCategory)
    fun getVersionCategory(): VersionCategory
}