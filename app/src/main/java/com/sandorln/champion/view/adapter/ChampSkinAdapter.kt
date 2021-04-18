package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sandorln.champion.databinding.ItemChampionSkinBinding
import com.sandorln.champion.model.ChampionData

class ChampSkinAdapter(var championData: ChampionData = ChampionData()) : RecyclerView.Adapter<ChampSkinAdapter.ChampSkinViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampSkinViewHolder =
        ChampSkinViewHolder(ItemChampionSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = championData.cSkins.size

    override fun onBindViewHolder(holder: ChampSkinViewHolder, position: Int) {
        with(holder.binding) {
            Glide.with(root)
                .load("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/${championData.cId}_${championData.cSkins[position].skNum}.jpg")
                .thumbnail(0.1f)
                .into(imgChampionFull)

            /* 스킨 명이 default 일 시 기본 챔피언 이름을 보여줌 */
            tvSkinName.text =
                if (championData.cSkins[position].skName == "default") championData.cName
                else championData.cSkins[position].skName
        }
    }

    class ChampSkinViewHolder(val binding: ItemChampionSkinBinding) : RecyclerView.ViewHolder(binding.root)
}