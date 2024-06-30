package com.sandorln.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sandorln.database.model.ItemEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
class MapsConverters @Inject constructor() {
    @TypeConverter
    fun toMapTypeEntity(value: Int) = enumValues<ItemEntity.MapTypeEntity>()[value]

    @TypeConverter
    fun fromMapTypeEntity(value: ItemEntity.MapTypeEntity) = value.ordinal
}