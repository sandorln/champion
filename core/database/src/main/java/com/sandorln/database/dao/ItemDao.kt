package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.ItemEntity
import com.sandorln.database.model.SummaryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM ItemEntity WHERE version == :version")
    fun getAllItemData(version: String): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemDataList(championList: List<ItemEntity>)

    @Query("SELECT id, name, image, tags, maps FROM ItemEntity WHERE version == :version AND inStore == 1")
    fun getAllSummaryItemData(version: String): Flow<List<SummaryItemEntity>>
}