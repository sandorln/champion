package com.sandorln.database.model

import android.graphics.Bitmap
import androidx.room.Entity

@Entity(primaryKeys = ["fileName", "version"])
data class SpriteImageEntity(
    val fileName: String = "",
    val version: String = "",
    val image: Bitmap? = null
)