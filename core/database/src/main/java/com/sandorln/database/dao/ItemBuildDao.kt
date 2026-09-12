package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.ItemBuildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemBuildDao {
    @Query("SELECT * FROM item_build WHERE version == :version ORDER BY id DESC")
    fun getItemBuildListByVersion(version: String): Flow<List<ItemBuildEntity>>

    @Query("SELECT * FROM item_build WHERE id == :id")
    fun getItemBuildById(id: Long): Flow<ItemBuildEntity?>

    @Query("SELECT count(*) FROM item_build WHERE version == :version")
    suspend fun getItemBuildCountByVersion(version: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItemBuild(itemBuildEntity: ItemBuildEntity): Long

    @Query("DELETE FROM item_build WHERE id == :id")
    suspend fun deleteItemBuildById(id: Long)
}
