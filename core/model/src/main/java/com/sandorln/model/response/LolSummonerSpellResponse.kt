package com.sandorln.model.response

import com.google.gson.Gson
import com.sandorln.model.SummonerSpell

data class LolSummonerSpellResponse(
    val summonerSpellList: MutableList<SummonerSpell> = mutableListOf()
) : BaseLolResponse() {
    override fun parsingData(languageCode: String) {
        val spellVersion = version

        val gson = Gson()
        for ((_, value) in data.entrySet()) {
            val itemData = gson.fromJson(value, SummonerSpell::class.java).apply {
                this.version = spellVersion
                this.languageCode = languageCode
            }
            summonerSpellList.add(itemData)
        }
    }
}