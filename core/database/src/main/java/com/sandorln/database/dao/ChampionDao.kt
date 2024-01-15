package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sandorln.database.model.ChampionEntity

@Dao
interface ChampionDao {
    /* TODO :: 가져 오는 곳의 값을 Model Module 에서 사용 하는 값으로 변경 필요 */
    @Query("SELECT * FROM ChampionEntity WHERE version == :version ORDER BY name")
    fun getAllChampion(version: String, languageCode: String): List<ChampionEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertChampionList(championList: List<ChampionEntity>)
}