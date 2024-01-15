package com.sandorln.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.database.model.ItemEntity.GoldEntity
import com.sandorln.database.model.ItemEntity.MapsEntity
import javax.inject.Inject

@ProvidedTypeConverter
class LolItemConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromGold(value: GoldEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toGold(value: String) = gson.fromJson(value, GoldEntity::class.java)

    @TypeConverter
    fun fromMaps(value: MapsEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toMaps(value: String) = gson.fromJson(value, MapsEntity::class.java)
}