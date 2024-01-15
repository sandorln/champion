package com.sandorln.champion.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.sandorln.model.ChampionData
import com.sandorln.model.ChampionData.ChampionSkin
import com.sandorln.model.ChampionData.ChampionSpell
import com.sandorln.model.ItemData
import com.sandorln.model.SummonerSpell

object DiffUtils {
    val DIFF_CHAMPION_DATA = object : DiffUtil.ItemCallback<com.sandorln.model.ChampionData>() {
        override fun areItemsTheSame(oldItem: com.sandorln.model.ChampionData, newItem: com.sandorln.model.ChampionData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: com.sandorln.model.ChampionData, newItem: com.sandorln.model.ChampionData): Boolean =
            oldItem == newItem
    }

    val DIFF_CHAMPION_SKIN = object : DiffUtil.ItemCallback<ChampionSkin>() {
        override fun areItemsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem == newItem
    }

    val DIFF_CHAMPION_SKILL = object : DiffUtil.ItemCallback<ChampionSpell>() {
        override fun areItemsTheSame(oldItem: ChampionSpell, newItem: ChampionSpell): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChampionSpell, newItem: ChampionSpell): Boolean = oldItem == newItem
    }

    val DIFF_ITEM_DATA = object : DiffUtil.ItemCallback<com.sandorln.model.ItemData>() {
        override fun areItemsTheSame(oldItem: com.sandorln.model.ItemData, newItem: com.sandorln.model.ItemData): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: com.sandorln.model.ItemData, newItem: com.sandorln.model.ItemData): Boolean = oldItem == newItem
    }

    val DIFF_SUMMONER_SPELL = object : DiffUtil.ItemCallback<com.sandorln.model.SummonerSpell>() {
        override fun areItemsTheSame(oldItem: com.sandorln.model.SummonerSpell, newItem: com.sandorln.model.SummonerSpell): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: com.sandorln.model.SummonerSpell, newItem: com.sandorln.model.SummonerSpell): Boolean = oldItem == newItem
    }
}