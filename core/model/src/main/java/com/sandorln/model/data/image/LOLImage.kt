package com.sandorln.model.data.image

import android.graphics.Bitmap

data class LOLImage(
    var full: String = "",
    var sprite: String = "",
    var group: String = "",
    /* 이미지 좌표 */
    var x: Int = 0,
    var y: Int = 0,
    var w: Int = 0,
    var h: Int = 0
) {
    fun getImageBitmap(spriteMap: Map<String, Bitmap?>): Bitmap? = runCatching {
        val originalBitmap = spriteMap[sprite] ?: return null

        Bitmap.createBitmap(originalBitmap, x, y, w, h)
    }.fold(
        onSuccess = { it },
        onFailure = { null }
    )
}