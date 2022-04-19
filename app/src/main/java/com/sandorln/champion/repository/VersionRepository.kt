package com.sandorln.champion.repository

import com.sandorln.champion.model.VersionCategory
import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    fun getLolVersionCategory(): Flow<VersionCategory>
}