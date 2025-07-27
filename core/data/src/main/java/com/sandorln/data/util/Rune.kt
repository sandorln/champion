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

fun RuneDataEntity.asRuneData(): RuneData = RuneData(
    id = this.id,
    key = this.key,
    icon = this.icon,
    name = this.name,
    shortDesc = this.shortDesc,
    longDesc = this.longDesc
)

fun RuneSlotEntity.asRuneSlot(): RuneSlot = RuneSlot(
    runes = this.runes.map { it.asRuneData() }
)

fun RuneStyleEntity.asRuneStyle(): RuneStyle =
    RuneStyle(
        id = this.id,
        key = this.key,
        icon = this.icon,
        name = this.name,
        slots = this.slots.map { it.asRuneSlot() }
    )

fun List<RuneStyleEntity>.asRuneStyleList(): List<RuneStyle> =
    this.map { it.asRuneStyle() }

fun NetworkRuneData.asRuneDataEntity(): RuneDataEntity =
    RuneDataEntity(
        id = this.id,
        key = this.key,
        icon = this.icon,
        name = this.name,
        shortDesc = this.shortDesc,
        longDesc = this.longDesc
    )

fun NetworkRuneSlot.asRuneSlotEntity(): RuneSlotEntity = RuneSlotEntity(
    runes = this.runes.map { it.asRuneDataEntity() }
)

fun NetworkRuneStyle.asRuneStyleEntity(version: String): RuneStyleEntity = RuneStyleEntity(
    id = this.id,
    version = version,
    key = this.key,
    icon = this.icon,
    name = this.name,
    slots = this.slots.map { it.asRuneSlotEntity() }
)

fun List<NetworkRuneStyle>.asRuneStyleEntityList(version: String): List<RuneStyleEntity> =
    this.map { it.asRuneStyleEntity(version) }