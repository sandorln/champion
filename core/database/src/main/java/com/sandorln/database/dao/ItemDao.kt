package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.ItemEntity

@Dao
interface ItemDao {
    /* TODO :: 가져 오는 곳의 값을 Model Module 에서 사용 하는 값으로 변경 필요 */
    @Query("SELECT * FROM ItemEntity WHERE version == :version")
    fun getAllItemData(version: String, languageCode: String): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemDataList(championList: List<ItemEntity>)
}