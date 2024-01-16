package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sandorln.database.model.SpriteImageEntity

@Dao
interface SpriteDao {
    @Query("SELECT * FROM SpriteImageEntity WHERE version == :version")
    fun getSpriteImageList(version: String): List<SpriteImageEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertSpriteImageList(spriteImageList: List<SpriteImageEntity>)
}