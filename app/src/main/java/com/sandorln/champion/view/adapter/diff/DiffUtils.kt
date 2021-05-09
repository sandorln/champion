package com.sandorln.champion.view.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSkin

object DiffUtils {
    val DIFF_CHAMPION_DATA = object : DiffUtil.ItemCallback<ChampionData>() {
        override fun areItemsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
            oldItem.cId == newItem.cId

        override fun areContentsTheSame(oldItem: ChampionData, newItem: ChampionData): Boolean =
            oldItem == newItem
    }

    val DIFF_CHAMPION_SKIN = object : DiffUtil.ItemCallback<ChampionSkin>() {
        override fun areItemsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem.skId == newItem.skId
        override fun areContentsTheSame(oldItem: ChampionSkin, newItem: ChampionSkin): Boolean = oldItem == newItem
    }
}