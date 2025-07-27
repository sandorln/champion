package com.sandorln.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.database.model.RuneDataEntity
import com.sandorln.database.model.RuneSlotEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
class LolRuneConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromRuneSlotEntityList(value: List<RuneSlotEntity>): String =
        gson.toJson(value)

    @TypeConverter
    fun toRuneSlotEntityList(value: String): List<RuneSlotEntity> =
        gson.fromJson(value, Array<RuneSlotEntity>::class.java).toList()

    @TypeConverter
    fun fromRuneDataEntityList(value: List<RuneDataEntity>): String =
        gson.toJson(value)

    @TypeConverter
    fun toRuneDataEntityList(value: String): List<RuneDataEntity> =
        gson.fromJson(value, Array<RuneDataEntity>::class.java).toList()
}