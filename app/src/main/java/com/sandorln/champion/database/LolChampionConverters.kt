package com.sandorln.champion.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.champion.model.*
import com.sandorln.champion.model.ChampionData.ChampionSpell
import com.sandorln.champion.model.ChampionData.ChampionSkin
import com.sandorln.champion.model.ChampionData.ChampionStats
import com.sandorln.champion.model.ChampionData.ChampionInfo
import javax.inject.Inject

@ProvidedTypeConverter
class LolChampionConverters @Inject constructor(private val gson: Gson) {
    @TypeConverter
    fun fromChampionInfo(value: ChampionInfo) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionInfo(value: String) = gson.fromJson(value, ChampionInfo::class.java)

    @TypeConverter
    fun fromLolImage(value: LOLImage) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toLolImage(value: String) = gson.fromJson(value, LOLImage::class.java)

    @TypeConverter
    fun fromStringList(value: List<String>) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toStringList(value: String) = gson.fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun fromChampionStats(value: ChampionStats) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionStats(value: String) = gson.fromJson(value, ChampionStats::class.java)

    @TypeConverter
    fun fromChampionSpell(value: ChampionSpell) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionSpell(value: String) = gson.fromJson(value, ChampionSpell::class.java)

    @TypeConverter
    fun fromChampionSpellList(value: List<ChampionSpell>) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionSpellList(value: String) = gson.fromJson(value, Array<ChampionSpell>::class.java).toList()

    @TypeConverter
    fun fromChampionSkinList(value: List<ChampionSkin>) = gson.toJson(value) ?: ""

    @TypeConverter
    fun toChampionSkinList(value: String) = gson.fromJson(value, Array<ChampionSkin>::class.java).toList()
}