package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.model.data.item.ItemData
import com.sandorln.network.model.NetworkItem

fun ItemEntity.asData(): ItemData = ItemData(
    id = id,
    version = version,
    name = name,
    description = description,
    depth = depth,
    inStore = inStore,
    from = from,
    into = into,
    image = image.asData()
)

fun NetworkItem.asEntity(id: String, version: String): ItemEntity = ItemEntity(
    id = id,
    version = version,
    name = name,
    description = description,
    depth = depth,
    inStore = inStore,
    from = from.filterNotNull(),
    into = into.filterNotNull(),
    image = image.asEntity()
)