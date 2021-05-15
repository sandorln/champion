package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionTipsBinding
import com.sandorln.champion.model.type.TipsType

/**
 * AllyTips / EnemyTips 두 가지의 팁 유형을 처리
 */
class ChampionTipsAdapter(var tips: List<String>, val tipsType: TipsType) : RecyclerView.Adapter<ChampionTipsAdapter.ChampionTipsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionTipsViewHolder =
        ChampionTipsViewHolder(ItemChampionTipsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionTipsViewHolder, position: Int) {
        holder.binding.tvTitle.visibility = if (position == 0) {
            holder.binding.tvTitle.text = tipsType.value
            View.VISIBLE
        } else
            View.GONE

        holder.binding.tvTips.text = tips[position]
    }

    override fun getItemCount(): Int = tips.size

    class ChampionTipsViewHolder(val binding: ItemChampionTipsBinding) : RecyclerView.ViewHolder(binding.root)
}