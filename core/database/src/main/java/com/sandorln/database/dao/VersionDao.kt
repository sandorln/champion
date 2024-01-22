package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.VersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Query("SELECT * FROM VersionEntity")
    fun getAllVersion(): Flow<List<VersionEntity>>

    @Query("SELECT * FROM VersionEntity WHERE name == :versionName")
    fun getVersionEntity(versionName : String) : Flow<List<VersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(versionEntity: VersionEntity)
}