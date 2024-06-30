package com.sandorln.item.util

import com.sandorln.item.R
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType

fun MapType.getTitleStringId(): Int = when (this) {
    MapType.ALL -> R.string.map_type_all
    MapType.SUMMONER_RIFT -> R.string.map_type_summoner_rift
    MapType.ARAM -> R.string.map_type_aram
    MapType.NONE -> R.string.map_type_none
}

fun ItemTagType.getTitleStringId(): Int = when (this) {
    ItemTagType.Damage -> R.string.item_stats_damage
    ItemTagType.AttackSpeed -> R.string.item_stats_attackspeed
    ItemTagType.CriticalStrike -> R.string.item_stats_criticalstrike
    ItemTagType.ArmorPenetration -> R.string.item_stats_armorpenetration
    ItemTagType.SpellDamage -> R.string.item_stats_spelldamage
    ItemTagType.ManaRegen -> R.string.item_stats_manaregen
    ItemTagType.Mana -> R.string.item_stats_mana
    ItemTagType.MagicPenetration -> R.string.item_stats_magicpenetration
    ItemTagType.Armor -> R.string.item_stats_armor
    ItemTagType.SpellBlock -> R.string.item_stats_spellblock
    ItemTagType.Health -> R.string.item_stats_health
    ItemTagType.HealthRegen -> R.string.item_stats_healthregen
    ItemTagType.CooldownReduction -> R.string.item_stats_cooldownreduction
    ItemTagType.NonbootsMovement -> R.string.item_stats_nonbootsmovement
    ItemTagType.LifeSteal -> R.string.item_stats_lifesteal
    ItemTagType.Boots -> R.string.item_stats_boots
    ItemTagType.Consumable -> R.string.item_stats_consumable
}