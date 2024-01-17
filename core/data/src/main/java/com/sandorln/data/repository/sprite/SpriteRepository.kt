package com.sandorln.data.repository.sprite

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface SpriteRepository {
    val currentSpriteFileMap: Flow<Map<String, Bitmap?>>

    suspend fun refreshSpriteBitmap(version: String, fileNameList: List<String>): Result<Any>
}