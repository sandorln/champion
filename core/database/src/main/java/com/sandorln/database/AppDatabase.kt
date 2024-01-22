package com.sandorln.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandorln.database.converter.BitmapConverters
import com.sandorln.database.converter.LolChampionConverters
import com.sandorln.database.converter.LolItemConverters
import com.sandorln.database.dao.ChampionDao
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.dao.SpriteImageDao
import com.sandorln.database.dao.SummonerSpellDao
import com.sandorln.database.dao.VersionDao
import com.sandorln.database.model.ChampionEntity
import com.sandorln.database.model.ItemEntity
import com.sandorln.database.model.SpriteImageEntity
import com.sandorln.database.model.SummonerSpellEntity
import com.sandorln.database.model.VersionEntity

@Database(
    entities = [
        ChampionEntity::class,
        ItemEntity::class,
        SummonerSpellEntity::class,
        VersionEntity::class,
        SpriteImageEntity::class
    ],
    version = 10
)
@TypeConverters(value = [LolChampionConverters::class, LolItemConverters::class, BitmapConverters::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao
    abstract fun itemDao(): ItemDao
    abstract fun summonerSpellDao(): SummonerSpellDao
    abstract fun versionDao(): VersionDao
    abstract fun spriteImageDao() : SpriteImageDao
}