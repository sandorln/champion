package com.sandorln.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.database.model.SummonerSpellEntity

@Dao
interface SummonerSpellDao {
    @Query("SELECT * FROM SummonerSpellEntity WHERE version == :version")
    fun getAllSummonerSpell(version: String, languageCode: String): List<SummonerSpellEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummonerSpellList(summonerSpellList: List<SummonerSpellEntity>)
}