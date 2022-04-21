package com.sandorln.champion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.database.roomdao.ItemDao
import com.sandorln.champion.database.roomdao.SummonerSpellDao
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.SummonerSpell

@Database(entities = [ChampionData::class, ItemData::class, SummonerSpell::class], version = 4)
@TypeConverters(value = [LolChampionConverters::class, LolItemConverters::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao
    abstract fun itemDao(): ItemDao
    abstract fun summonerSpellDao(): SummonerSpellDao
}