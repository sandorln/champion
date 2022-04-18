package com.sandorln.champion.database.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.sandorln.champion.model.ChampionData
import kotlinx.coroutines.flow.Flow

@Dao
interface ChampionDao {
    @Query("SELECT * FROM ChampionData WHERE version == :version")
    fun getChampionList(version: String): Flow<ChampionData>

    @Insert(onConflict = REPLACE)
    suspend fun insertChampionList(championList: List<ChampionData>)
}