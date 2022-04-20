package com.sandorln.champion.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.champion.model.ItemData.Gold
import com.sandorln.champion.model.ItemData.Maps
import javax.inject.Inject

@ProvidedTypeConverter
class LolItemConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromGold(value: Gold) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toGold(value: String) = gson.fromJson(value, Gold::class.java)

    @TypeConverter
    fun fromMaps(value: Maps) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toMaps(value: String) = gson.fromJson(value, Maps::class.java)
}