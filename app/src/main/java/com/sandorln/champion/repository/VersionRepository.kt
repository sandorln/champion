package com.sandorln.champion.repository

import com.sandorln.champion.model.VersionLol.VersionCategory
import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    fun getLolVersionCategory(): Flow<VersionCategory>
}