package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionFullSkinBinding
import com.sandorln.model.ChampionData.ChampionSkin
import com.sandorln.champion.util.setChampionSplash
import com.sandorln.champion.view.adapter.diff.DiffUtils

/**
 * 정 사이즈 Champion Skin Adapter
 */
class ChampionFullSkinAdapter(var championId: String = "") :
    ListAdapter<ChampionSkin, ChampionFullSkinAdapter.ChampionFullSkinViewHolder>(DiffUtils.DIFF_CHAMPION_SKIN) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionFullSkinViewHolder =
        ChampionFullSkinViewHolder(ItemChampionFullSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionFullSkinViewHolder, position: Int) {
        val skin = getItem(position)
        if (championId.isNotEmpty()) {
            holder.binding.imgChampionSkin.setChampionSplash(championId, skin.num ?: position.toString())
            holder.binding.tvSkinName.text = if (position == 0) "기본 스킨" else skin.name
        }
    }

    class ChampionFullSkinViewHolder(val binding: ItemChampionFullSkinBinding) : RecyclerView.ViewHolder(binding.root)
}