package com.sandorln.database.model.base

data class LOLImageEntity(
    var sprite: String = "",
    /* 이미지 좌표 */
    var x: Int = 0,
    var y: Int = 0,
    var w: Int = 0,
    var h: Int = 0
)