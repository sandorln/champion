package com.sandorln.data.util

import com.sandorln.database.model.RuneDataEntity
import com.sandorln.database.model.RuneSlotEntity
import com.sandorln.database.model.RuneStyleEntity
import com.sandorln.model.data.rune.RuneData
import com.sandorln.model.data.rune.RuneSlot
import com.sandorln.model.data.rune.RuneStyle
import com.sandorln.network.model.rune.NetworkRuneData
import com.sandorln.network.model.rune.NetworkRuneSlot
import com.sandorln.network.model.rune.NetworkRuneStyle

fun RuneDataEntity.asData(): RuneData = RuneData(
    id = this.id,
    key = this.key,
    icon = this.icon,
    name = this.name,
    shortDesc = this.shortDesc,
    longDesc = this.longDesc
)

fun RuneSlotEntity.asData(): RuneSlot = RuneSlot(
    runes = this.runes.map(RuneDataEntity::asData)
)

fun RuneStyleEntity.asData(): RuneStyle =
    RuneStyle(
        id = this.id,
        key = this.key,
        icon = this.icon,
        name = this.name,
        slots = this.slots.map(RuneSlotEntity::asData)
    )

fun NetworkRuneData.toEntity(): RuneDataEntity =
    RuneDataEntity(
        id = this.id,
        key = this.key,
        icon = this.icon,
        name = this.name,
        shortDesc = this.shortDesc,
        longDesc = this.longDesc
    )

fun NetworkRuneSlot.toEntity(): RuneSlotEntity = RuneSlotEntity(
    runes = this.runes.map(NetworkRuneData::toEntity)
)

fun NetworkRuneStyle.toEntity(version: String): RuneStyleEntity = RuneStyleEntity(
    id = this.id,
    version = version,
    key = this.key,
    icon = this.icon,
    name = this.name,
    slots = this.slots.map(NetworkRuneSlot::toEntity)
)

fun List<NetworkRuneStyle>.toEntityList(version: String): List<RuneStyleEntity> =
    this.map { it.toEntity(version) }