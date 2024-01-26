package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sandorln.database.model.VersionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@Dao
interface VersionDao {
    @Query("SELECT * FROM VersionEntity")
    fun getAllVersion(): Flow<List<VersionEntity>>

    @Query("SELECT * FROM VersionEntity WHERE name == :versionName")
    fun getVersionEntity(versionName: String): Flow<List<VersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(versionEntity: VersionEntity)

    @Update
    suspend fun updateVersion(versionEntity: VersionEntity)
}

suspend fun VersionDao.getAllVersionOrderByDesc() : List<VersionEntity> = getAllVersion().map { versionList ->
    val versionComparator = Comparator<VersionEntity> { version1, version2 ->
        val (major1, minor1, patch1) = version1.name.split('.').map { it.toInt() }
        val (major2, minor2, patch2) = version2.name.split('.').map { it.toInt() }

        when {
            major1 != major2 -> major2 - major1
            minor1 != minor2 -> minor2 - minor1
            else -> patch2 - patch1
        }
    }
    versionList.sortedWith(versionComparator)
}.firstOrNull() ?: emptyList()