package com.sandorln.model

data class LOLImage(
    var full: String = "",
    var sprite: String = "",
    var group: String = "",
    /* 이미지 좌표 */
    var x: Int = 0,
    var y: Int = 0,
    var w: Int = 0,
    var h: Int = 0
)