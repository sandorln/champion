package com.sandorln.champion.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.champion.model.*

class LolChampionConverters {
    @TypeConverter
    fun championInfoToJson(value: ChampionData.ChampionInfo) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToChampionInfo(value: String) = Gson().fromJson(value, ChampionData.ChampionInfo::class.java)

    @TypeConverter
    fun lolImageToJson(value: LOLImage) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToLolImage(value: String) = Gson().fromJson(value, LOLImage::class.java)

    @TypeConverter
    fun stringListToJson(value: List<String>) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToStringList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun championStatsToJson(value: ChampionData.ChampionStats) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToChampionStats(value: String) = Gson().fromJson(value, ChampionData.ChampionStats::class.java)

    @TypeConverter
    fun championSpellToJson(value: ChampionData.ChampionSpell) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToChampionSpell(value: String) = Gson().fromJson(value, ChampionData.ChampionSpell::class.java)

    @TypeConverter
    fun championSpellListToJson(value: List<ChampionData.ChampionSpell>) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToChampionSpellList(value: String) = Gson().fromJson(value, Array<ChampionData.ChampionSpell>::class.java).toList()

    @TypeConverter
    fun championSkinListToJson(value: List<ChampionData.ChampionSkin>) = Gson().toJson(value) ?: ""

    @TypeConverter
    fun JsonToChampionSkinList(value: String) = Gson().fromJson(value, Array<ChampionData.ChampionSkin>::class.java).toList()

}