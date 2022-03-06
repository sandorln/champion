package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionTipBinding

/**
 * AllyTips / EnemyTips 두 가지의 팁 유형을 처리
 */
class ChampionTipAdapter(var tips: List<String> = mutableListOf()) : RecyclerView.Adapter<ChampionTipAdapter.ChampionTipViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionTipViewHolder =
        ChampionTipViewHolder(ItemChampionTipBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionTipViewHolder, position: Int) {
        holder.binding.tvTips.text = tips[position]
    }

    override fun getItemCount(): Int = tips.size

    class ChampionTipViewHolder(val binding: ItemChampionTipBinding) : RecyclerView.ViewHolder(binding.root)
}