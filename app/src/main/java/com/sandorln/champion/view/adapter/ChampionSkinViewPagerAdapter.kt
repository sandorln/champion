package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionFullSkinBinding
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.view.binding.setChampionSkin

class ChampionSkinViewPagerAdapter(var championId: String = "0", var skinList: List<ChampionSkin> = mutableListOf()) :
    RecyclerView.Adapter<ChampionSkinViewPagerAdapter.ChampionFullSkinViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionFullSkinViewHolder =
        ChampionFullSkinViewHolder(ItemChampionFullSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionFullSkinViewHolder, position: Int) {
        holder.binding.imgChampionSkin.setChampionSkin(championId, skinList[position].skNum)
    }

    override fun getItemCount(): Int = skinList.size

    class ChampionFullSkinViewHolder(val binding: ItemChampionFullSkinBinding) : RecyclerView.ViewHolder(binding.root)
}