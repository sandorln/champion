package com.sandorln.champion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.model.ChampionData

@Database(entities = [ChampionData::class], version = 1)
@TypeConverters(LolChampionConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao
}