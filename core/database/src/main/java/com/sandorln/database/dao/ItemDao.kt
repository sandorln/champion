package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM ItemEntity WHERE version == :version")
    fun getAllItemData(version: String): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemDataList(championList: List<ItemEntity>)

    @Query(
        "SELECT * FROM itementity WHERE version == :currentVersion\n" +
                "AND id NOT IN (SELECT id FROM itementity WHERE version == :preVersion)\n" +
                "AND name NOT IN (SELECT name FROM itementity WHERE version == :preVersion);"
    )
    suspend fun getDifferenceItemListByVersion(currentVersion: String, preVersion: String): List<ItemEntity>
}