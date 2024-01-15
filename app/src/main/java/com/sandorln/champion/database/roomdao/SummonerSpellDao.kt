package com.sandorln.champion.database.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandorln.model.SummonerSpell

@Dao
interface SummonerSpellDao {
    @Query("SELECT * FROM SummonerSpell WHERE version == :version AND languageCode == :languageCode")
    fun getAllSummonerSpell(version: String, languageCode: String): List<com.sandorln.model.SummonerSpell>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummonerSpellList(summonerSpellList: List<com.sandorln.model.SummonerSpell>)
}