package com.sandorln.champion.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.sandorln.model.data.champion.ChampionData
import com.sandorln.model.data.champion.ChampionSkin
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.spell.SummonerSpell

object DiffUtils {
    val DIFF_CHAMPION_DATA = object : DiffUtil.ItemCallback<ChampionData>() {
        override fun areItemsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
            oldItem == newItem
    }

    val DIFF_SUMMARY_CHAMPION_DATA = object : DiffUtil.ItemCallback<SummaryChampion>() {
        override fun areItemsTheSame(oldItem: SummaryChampion, newItem: SummaryChampion): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SummaryChampion, newItem: SummaryChampion): Boolean = oldItem == newItem
    }

    val DIFF_CHAMPION_SKIN = object : DiffUtil.ItemCallback<ChampionSkin>() {
        override fun areItemsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem == newItem
    }

    val DIFF_CHAMPION_SKILL = object : DiffUtil.ItemCallback<ChampionSpell>() {
        override fun areItemsTheSame(oldItem: ChampionSpell, newItem: ChampionSpell): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChampionSpell, newItem: ChampionSpell): Boolean = oldItem == newItem
    }

    val DIFF_ITEM_DATA = object : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean = oldItem == newItem
    }

    val DIFF_SUMMONER_SPELL = object : DiffUtil.ItemCallback<SummonerSpell>() {
        override fun areItemsTheSame(oldItem: SummonerSpell, newItem: SummonerSpell): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SummonerSpell, newItem: SummonerSpell): Boolean = oldItem == newItem
    }
}