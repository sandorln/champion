package com.sandorln.champion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.database.roomdao.ItemDao
import com.sandorln.champion.database.roomdao.SummonerSpellDao
import com.sandorln.model.ChampionData
import com.sandorln.model.ItemData
import com.sandorln.model.SummonerSpell

@Database(entities = [com.sandorln.model.ChampionData::class, com.sandorln.model.ItemData::class, com.sandorln.model.SummonerSpell::class], version = 7)
@TypeConverters(value = [LolChampionConverters::class, LolItemConverters::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao
    abstract fun itemDao(): ItemDao
    abstract fun summonerSpellDao(): SummonerSpellDao
}