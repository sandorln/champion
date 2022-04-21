package com.sandorln.champion.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionData.ChampionSkin
import com.sandorln.champion.model.ChampionData.ChampionSpell
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.SummonerSpell

object DiffUtils {
    val DIFF_CHAMPION_DATA = object : DiffUtil.ItemCallback<ChampionData>() {
        override fun areItemsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
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

    val DIFF_ITEM_DATA = object : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean = oldItem == newItem
    }

    val DIFF_SUMMONER_SPELL = object : DiffUtil.ItemCallback<SummonerSpell>() {
        override fun areItemsTheSame(oldItem: SummonerSpell, newItem: SummonerSpell): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SummonerSpell, newItem: SummonerSpell): Boolean = oldItem == newItem
    }
}