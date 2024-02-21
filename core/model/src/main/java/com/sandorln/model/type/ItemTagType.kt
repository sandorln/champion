package com.sandorln.model.type

enum class ItemTagType(val typeName: String) {
    Boots("장화"),
    Consumable("소모성"),

    /* 공격 */
    Damage("공격력"),
    AttackSpeed("공격속도"),
    CriticalStrike("치명타확률"),
    ArmorPenetration("물리관통력"),

    /* 마나 */
    SpellDamage("주문력"),
    ManaRegen("마나재생"),
    Mana("마나"),
    MagicPenetration("마법관통력"),

    /* 방어 */
    Armor("방어력"),
    SpellBlock("마법저항력"),
    Health("체력"),
    HealthRegen("체력재생"),

    /* 그 외 */
    CooldownReduction("스킬가속"),
    NonbootsMovement("이동속도"),
    LifeSteal("흡혈");

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