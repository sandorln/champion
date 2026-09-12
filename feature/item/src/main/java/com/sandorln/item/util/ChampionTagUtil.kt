package com.sandorln.item.util

import com.sandorln.item.R
import com.sandorln.model.type.ChampionTag

fun ChampionTag.getTitleStringRes(): Int = when (this) {
    ChampionTag.Fighter -> R.string.tag_fighter
    ChampionTag.Tank -> R.string.tag_tank
    ChampionTag.Mage -> R.string.tag_mage
    ChampionTag.Assassin -> R.string.tag_assassin
    ChampionTag.Marksman -> R.string.tag_marksman
    ChampionTag.Support -> R.string.tag_support
}

fun ChampionTag.getIconRes(): Int = when (this) {
    ChampionTag.Fighter -> com.sandorln.design.R.drawable.ic_tag_fighter
    ChampionTag.Tank -> com.sandorln.design.R.drawable.ic_tag_tank
    ChampionTag.Mage -> com.sandorln.design.R.drawable.ic_tag_mage
    ChampionTag.Assassin -> com.sandorln.design.R.drawable.ic_tag_assassin
    ChampionTag.Marksman -> com.sandorln.design.R.drawable.ic_tag_marksman
    ChampionTag.Support -> com.sandorln.design.R.drawable.ic_tag_support
}
