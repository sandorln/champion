package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sandorln.database.model.SpriteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpriteImageDao {
    @Query("SELECT * FROM SpriteImageEntity WHERE version == :version")
    fun getSpriteImageList(version: String): Flow<List<SpriteImageEntity>>

    @Query("SELECT count(*) FROM SpriteImageEntity WHERE version == :version AND fileName == :fileName")
    suspend fun hasSpriteImage(version: String, fileName: String): Int

    @Insert(onConflict = REPLACE)
    suspend fun insertSpriteImageList(spriteImageList: List<SpriteImageEntity>)
}