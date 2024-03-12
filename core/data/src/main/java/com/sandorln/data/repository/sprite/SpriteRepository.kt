package com.sandorln.data.repository.sprite

import android.graphics.Bitmap
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.version.Version
import kotlinx.coroutines.flow.Flow

interface SpriteRepository {
    suspend fun refreshDownloadSpriteBitmap(
        version: Version,
        spriteType: SpriteType,
        fileNameList: List<String>
    ): Result<Any>

    suspend fun getSpriteBitmap(version: String): Map<String, Bitmap>
}