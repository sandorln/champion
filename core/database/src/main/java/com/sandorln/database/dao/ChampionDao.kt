package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sandorln.database.model.ChampionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChampionDao {
    @Query("SELECT * FROM ChampionEntity WHERE version == :version ORDER BY name")
    fun getChampionList(version: String): Flow<List<ChampionEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertChampionList(championList: List<ChampionEntity>)
}