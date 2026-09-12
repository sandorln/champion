package com.sandorln.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sandorln.database.converter.LolChampionConverters
import com.sandorln.database.dao.ItemBuildDao
import com.sandorln.database.model.ItemBuildEntity

@Database(
    entities = [
        ItemBuildEntity::class
    ],
    version = 1
)
@TypeConverters(value = [LolChampionConverters::class])
abstract class ItemBuildDatabase : RoomDatabase() {
    abstract fun itemBuildDao(): ItemBuildDao
}
