package com.sandorln.data.util

import com.sandorln.database.model.base.LOLImageEntity
import com.sandorln.model.data.image.LOLImage
import com.sandorln.network.model.NetworkLOLImage

fun LOLImageEntity.asData(): LOLImage = LOLImage(
    sprite = sprite,
    x = x,
    y = y,
    w = w,
    h = h
)

fun NetworkLOLImage.asEntity(): LOLImageEntity = LOLImageEntity(
    sprite = sprite,
    x = x,
    y = y,
    w = w,
    h = h
)

fun NetworkLOLImage.asData(): LOLImage = LOLImage(
    full = full,
    sprite = sprite,
    x = x,
    y = y,
    w = w,
    h = h
)