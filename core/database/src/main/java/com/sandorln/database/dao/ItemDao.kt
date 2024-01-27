package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
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

    @Query(
        "SELECT * FROM itementity WHERE version == :currentVersion " +
                "AND id NOT IN (SELECT id FROM itementity WHERE version == :preVersion) " +
                "AND name NOT IN (SELECT name FROM itementity WHERE version == :preVersion);"
    )
    suspend fun getNewItemDataList(currentVersion: String, preVersion: String): List<ItemEntity>

    @Query("SELECT count(*) FROM itementity WHERE version == :currentVersion " +
            "AND id NOT IN (SELECT id FROM itementity WHERE version == :preVersion) " +
            "AND name NOT IN (SELECT name FROM itementity WHERE version == :preVersion) " +
            "AND inStore == 1")
    suspend fun getNewItemCount(currentVersion: String, preVersion: String) : Int
}