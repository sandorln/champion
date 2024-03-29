package com.sandorln.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.database.model.ChampionEntity.*
import com.sandorln.database.model.ChampionTagEntity
import com.sandorln.database.model.base.LOLImageEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
class LolChampionConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromChampionInfo(value: ChampionInfoEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionInfo(value: String) = gson.fromJson(value, ChampionInfoEntity::class.java)

    @TypeConverter
    fun fromLolImage(value: LOLImageEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toLolImage(value: String) = gson.fromJson(value, LOLImageEntity::class.java)

    @TypeConverter
    fun fromStringList(value: List<String>) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toStringList(value: String): List<String> = gson.fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun fromNullStringList(value: List<String>?) = if (value != null) {
        gson.toJson(value)
    } else {
        null
    }

    @TypeConverter
    fun toNullStringList(value: String?): List<String>? = if (value != null) {
        gson.fromJson(value, Array<String>::class.java).toList()
    } else {
        null
    }

    @TypeConverter
    fun fromChampionStats(value: ChampionStatsEntity) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionStats(value: String) = gson.fromJson(value, ChampionStatsEntity::class.java)

    @TypeConverter
    fun fromChampionTag(value: List<ChampionTagEntity>) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionTag(value: String): List<ChampionTagEntity> =
        gson.fromJson(value, Array<String>::class.java)
            .toList()
            .mapNotNull {
                runCatching {
                    ChampionTagEntity.valueOf(it)
                }.getOrNull()
            }
}