package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionFullSkinBinding
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.view.adapter.diff.DiffUtils
import com.sandorln.champion.view.binding.setChampionSkin

/**
 * 정 사이즈 Champion Skin Adapter
 */
class ChampionFullSkinAdapter(var championId: String = "0") :
    ListAdapter<ChampionSkin, ChampionFullSkinAdapter.ChampionFullSkinViewHolder>(DiffUtils.DIFF_CHAMPION_SKIN) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionFullSkinViewHolder =
        ChampionFullSkinViewHolder(ItemChampionFullSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionFullSkinViewHolder, position: Int) {
        val skin = getItem(position)
        holder.binding.imgChampionSkin.setChampionSkin(championId, skin.skNum)
    }

    class ChampionFullSkinViewHolder(val binding: ItemChampionFullSkinBinding) : RecyclerView.ViewHolder(binding.root)
}