package com.sandorln.champion.database.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.champion.model.ItemData

@Dao
interface ItemDao {
    @Query("SELECT * FROM ItemData WHERE version == :version AND languageCode == :languageCode")
    fun getAllItemData(version: String, languageCode: String): List<ItemData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemDataList(championList: List<ItemData>)
}