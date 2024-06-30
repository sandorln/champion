package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.sandorln.database.model.ChampionEntity
import com.sandorln.database.model.ChampionTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChampionDao {
    @Query("SELECT * FROM ChampionEntity WHERE version == :version ORDER BY name")
    fun getChampionList(version: String): Flow<List<ChampionEntity>>

    @Query("SELECT * FROM ChampionEntity WHERE version == :version AND id IN (:idList)")
    suspend fun getChampionListByChampionIdList(version: String, idList: List<String>): List<ChampionEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertChampionList(championList: List<ChampionEntity>)

    @Query(
        "SELECT id FROM ChampionEntity WHERE version == :currentVersion " +
                "AND id NOT IN (SELECT id FROM ChampionEntity WHERE version == :preVersion) " +
                "AND name NOT IN (SELECT name FROM ChampionEntity WHERE version == :preVersion)"
    )
    suspend fun getNewChampionIdList(currentVersion: String, preVersion: String): List<String>

    @Query("SELECT * FROM ChampionEntity WHERE version == :version AND id == :championId")
    suspend fun getChampionEntity(version: String, championId: String): List<ChampionEntity>

    @Query("SELECT count(*) FROM ChampionEntity WHERE version == :version AND id == :championId")
    suspend fun hasChampionDetailData(version: String, championId: String): Int

    @Query("SELECT * FROM ChampionEntity WHERE version == :version AND tags == :tags")
    suspend fun getSimilarChampionList(version: String, tags: List<ChampionTagEntity>): List<ChampionEntity>

    @Query("SELECT version FROM championentity WHERE id == :championId")
    suspend fun getChampionVersionList(championId: String): List<String>

    @Query(
        "SELECT version FROM championentity WHERE id=:id " +
                "AND version=:newVersion " +
                "AND stats != (SELECT stats FROM championentity WHERE id=:id AND version=:oldVersion)"
    )
    suspend fun getChangedStatsVersionList(id: String, newVersion: String, oldVersion: String): List<String>
}