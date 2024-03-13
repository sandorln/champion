package com.sandorln.model.type

enum class ItemTagType {
    Boots,
    Consumable,

    /* 공격 */
    Damage,
    AttackSpeed,
    CriticalStrike,
    ArmorPenetration,

    /* 마나 */
    SpellDamage,
    ManaRegen,
    Mana,
    MagicPenetration,

    /* 방어 */
    Armor,
    SpellBlock,
    Health,
    HealthRegen,

    /* 그 외 */
    CooldownReduction,
    NonbootsMovement,
    LifeSteal;

    enum class StyleType {
        ATTACK, MANA, ARMOR, ETC
    }

    fun getStyleType(): StyleType? = when {
        attackTagTypeList.contains(this) -> StyleType.ATTACK
        manaTagTypeList.contains(this) -> StyleType.MANA
        armorTagTypeList.contains(this) -> StyleType.ARMOR
        etcTagTypeList.contains(this) -> StyleType.ETC
        else -> null
    }

    companion object {
        private val attackTagTypeList = listOf(Damage, AttackSpeed, CriticalStrike, ArmorPenetration)
        private val manaTagTypeList = listOf(SpellDamage, Mana, ManaRegen, MagicPenetration)
        private val armorTagTypeList = listOf(Armor, SpellBlock, Health, HealthRegen)
        private val etcTagTypeList = listOf(CooldownReduction, NonbootsMovement, LifeSteal)
    }
}