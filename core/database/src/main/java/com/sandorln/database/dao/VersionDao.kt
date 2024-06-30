package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sandorln.database.model.VersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Query("SELECT * FROM VersionEntity")
    fun getAllVersion(): Flow<List<VersionEntity>>

    @Query("SELECT * FROM VersionEntity WHERE name == :versionName")
    fun getVersionEntity(versionName: String): Flow<List<VersionEntity>>

    @Query("SELECT * FROM VersionEntity WHERE isCompleteChampions == 0 OR isCompleteItems == 0 OR isCompleteSummonerSpell == 0")
    suspend fun getNotInitVersionEntityList(): List<VersionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(versionEntity: VersionEntity)

    @Update
    suspend fun updateVersion(versionEntity: VersionEntity)
}