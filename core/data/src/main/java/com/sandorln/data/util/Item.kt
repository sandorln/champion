package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.database.model.SummaryItemEntity
import com.sandorln.database.model.SummaryItemImageEntity
import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.item.SummaryItemImage
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import com.sandorln.network.model.item.NetworkItem

private fun isVersionAtLeast(version: String, major: Int, minor: Int): Boolean = runCatching {
    val v = version.split('.').map { it.toIntOrNull() ?: 0 }
    v[0] > major || (v[0] == major && v.getOrElse(1) { 0 } >= minor)
}.getOrDefault(false)

private val ARENA_FOUR_DIGIT_ID_SET = setOf(
    "2142", "2143", "2144", "2145", "2146",
    "3348", "3430", "4010", "4011", "4015", "4016", "4017"
)

private fun isArenaItem(id: String): Boolean =
    (id.length > 4 && (id.startsWith("22") || id.startsWith("44"))) ||
    ARENA_FOUR_DIGIT_ID_SET.contains(id)

fun ItemEntity.asData(): ItemData = ItemData(
    id = id,
    version = version,
    name = name,
    description = description,
    depth = depth,
    inStore = inStore,
    from = from,
    into = into,
    tags = tags.asItemTagTypeSet().let {
        if (id == "3172" && isVersionAtLeast(version, 14, 10)) it + ItemTagType.Boots else it
    },
    image = image.asData(),
    mapType = when {
        id.startsWith("77") -> MapType.CLASSIC
        id.length > 4 && id.startsWith("66") -> MapType.NONE
        isArenaItem(id) -> MapType.ARENA
        else -> maps.asData()
    },
    gold = gold.asData()
)

fun SummaryItemEntity.asData(): ItemData = ItemData(
    id = id,
    depth = depth,
    name = name,
    into = into,
    tags = tags.asItemTagTypeSet(),
    image = image.asData(),
    mapType = when {
        id.startsWith("77") -> MapType.CLASSIC
        id.length > 4 && id.startsWith("66") -> MapType.NONE
        isArenaItem(id) -> MapType.ARENA
        else -> maps.asData()
    }
)

fun NetworkItem.asEntity(id: String, version: String): ItemEntity = ItemEntity(
    id = id,
    version = version,
    name = name,
    description = description,
    depth = depth,
    inStore = inStore,
    tags = if (id == "3172" && isVersionAtLeast(version, 14, 10) && !tags.contains("Boots")) tags + "Boots" else tags,
    from = from.filterNotNull(),
    into = into.filterNotNull(),
    image = image.asEntity(),
    maps = when {
        id.startsWith("77") -> ItemEntity.MapTypeEntity.CLASSIC
        id.length > 4 && id.startsWith("66") -> ItemEntity.MapTypeEntity.NONE
        isArenaItem(id) -> ItemEntity.MapTypeEntity.ARENA
        else -> maps.asMapTypeEntity()
    },
    gold = gold.asEntity()
)

fun List<String>.asItemTagTypeSet(): Set<ItemTagType> {
    val tagSet = mutableSetOf<ItemTagType>()

    forEach { value ->
        val lowerValue = value.lowercase()

        /* 예외 또는 중복 태그 처리 */
        when (lowerValue) {
            "spellvamp" -> {
                tagSet.add(ItemTagType.LifeSteal)
                return@forEach
            }
        }

        val itemTagType = runCatching {
            ItemTagType.entries.firstOrNull { itemTagType ->
                lowerValue == itemTagType.name.lowercase()
            }
        }.getOrNull() ?: return@forEach

        tagSet.add(itemTagType)
    }

    return tagSet
}

fun ItemEntity.GoldEntity.asData() = ItemData.Gold(
    base = base,
    purchasable = purchasable,
    sell = sell,
    total = total
)

fun NetworkItem.NetworkGold.asEntity() = ItemEntity.GoldEntity(
    base = base,
    purchasable = purchasable,
    sell = sell,
    total = total
)

fun ItemEntity.asCombinationData(fromItemList: List<ItemCombination>) = ItemCombination(
    id = id,
    version = version,
    name = name,
    image = image.asData(),
    gold = gold.asData(),
    fromItemList = fromItemList
)

fun SummaryItemImageEntity.asData() = SummaryItemImage(
    id = id,
    image = image.asData()
)