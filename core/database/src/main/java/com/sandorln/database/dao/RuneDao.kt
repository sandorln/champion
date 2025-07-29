package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.RuneStyleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuneDao {
    @Query("SELECT * FROM RuneStyleEntity WHERE version == :version")
    fun getAllRuneStyleList(version: String): Flow<List<RuneStyleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuneStyleList(runeStyleList: List<RuneStyleEntity>)
}