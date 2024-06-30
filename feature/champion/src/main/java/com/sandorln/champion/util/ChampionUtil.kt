package com.sandorln.champion.util

import com.sandorln.design.theme.Colors
import com.sandorln.model.type.ChampionTag

internal fun ChampionTag.getResourceId(): Int = when (this) {
    ChampionTag.Fighter -> com.sandorln.design.R.drawable.ic_tag_fighter
    ChampionTag.Tank -> com.sandorln.design.R.drawable.ic_tag_tank
    ChampionTag.Mage -> com.sandorln.design.R.drawable.ic_tag_mage
    ChampionTag.Assassin -> com.sandorln.design.R.drawable.ic_tag_assassin
    ChampionTag.Marksman -> com.sandorln.design.R.drawable.ic_tag_marksman
    ChampionTag.Support -> com.sandorln.design.R.drawable.ic_tag_support
}

internal fun Double?.statusCompareColor(otherFloat: Double?) = when (this?.compareTo(otherFloat ?: this)) {
    -1 -> Colors.Blue03
    1 -> Colors.Orange00
    else -> Colors.BasicWhite
}