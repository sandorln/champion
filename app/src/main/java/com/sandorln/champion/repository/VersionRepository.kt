package com.sandorln.champion.repository

import com.sandorln.champion.model.VersionCategory

interface VersionRepository {
    suspend fun getLolVersionCategory() : VersionCategory
}