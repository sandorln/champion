package com.sandorln.champion.model.response

import com.google.gson.Gson
import com.sandorln.champion.model.SummonerSpell

data class LolSummonerSpellResponse(
    val summonerSpellList: MutableList<SummonerSpell> = mutableListOf()
) : BaseLolResponse() {
    override fun parsingData() {
        val spellVersion = version

        val gson = Gson()
        for ((_, value) in data.entrySet()) {
            val itemData = gson.fromJson(value, SummonerSpell::class.java).apply {
                this.version = spellVersion
            }
            summonerSpellList.add(itemData)
        }
    }
}