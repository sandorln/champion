package com.sandorln.model.data.champion

import android.graphics.Bitmap
import com.sandorln.model.data.image.LOLImage

data class SummaryChampion(
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var tags: List<String> = mutableListOf(),
    var partype: String = "",

    var info: ChampionInfo = ChampionInfo(),
    var image: LOLImage = LOLImage(),
    var stats: ChampionStats = ChampionStats()
) {
    fun getImageBitmap(spriteMap: Map<String, Bitmap?>): Bitmap? = runCatching {
        val imageInfo = image
        val originalBitmap = spriteMap[imageInfo.sprite] ?: return null

        Bitmap.createBitmap(
            originalBitmap,
            imageInfo.x,
            imageInfo.y,
            imageInfo.w,
            imageInfo.h
        )
    }.fold(
        onSuccess = { it },
        onFailure = { null }
    )
}