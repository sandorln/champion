package com.sandorln.data.repo.sprite

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface SpriteRepository {
    val currentVersionSpriteFileMap: Flow<Map<String, Bitmap?>>

    suspend fun refreshSpriteBitmap(version: String, fileNameList: List<String>): Result<Any>
}