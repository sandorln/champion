package com.sandorln.model.data.rune

data class RuneStyle(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val slots: List<RuneSlot>
) {
    val iconUrl = if (icon.startsWith("ASSETS")) {
        val iconKey = icon.split("/").lastOrNull()?.split(".")?.firstOrNull() ?: ""
        "https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles/$iconKey.png"
    } else {
        "https://ddragon.leagueoflegends.com/cdn/img/$icon"
    }
}