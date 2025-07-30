package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage
import com.sandorln.model.type.ChampionTag
import com.sandorln.model.type.SpellType
import java.util.Locale

data class ChampionDetailData(
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var lore: String = "",
    var info: ChampionInfo = ChampionInfo(),
    var image: LOLImage = LOLImage(),
    var tags: List<ChampionTag> = mutableListOf(),
    var partype: String = "",
    var stats: ChampionStats = ChampionStats(),
    var skins: List<ChampionSkin> = mutableListOf(),

    var spells: List<ChampionSpell> = mutableListOf(),
    var passive: ChampionSpell = ChampionSpell(),

    var allytips: List<String> = mutableListOf(),
    var enemytips: List<String> = mutableListOf()
) {
    companion object {
        private const val VIDEO_PREFIX = "https://d28xe8vt774jo5.cloudfront.net/champion-abilities/"
    }

    fun getVideoUrl(selectedSkill: ChampionSpell): String {
        val championKey = String.format(Locale.KOREAN, "%04d", key)
        val spellKeyName = selectedSkill.spellType.name
        val suffix = if (selectedSkill.spellType == SpellType.P) "mp4" else "webm"
        val url = "$VIDEO_PREFIX${championKey}/ability_${championKey}_${spellKeyName}1.$suffix"
        return url
    }
}