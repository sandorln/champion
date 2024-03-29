package com.sandorln.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.database.model.ItemEntity.GoldEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
class LolItemConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromGold(value: GoldEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toGold(value: String) = gson.fromJson(value, GoldEntity::class.java)
}