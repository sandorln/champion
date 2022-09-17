package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionStatusBinding

class ChampionStatusAdapter : RecyclerView.Adapter<ChampionStatusAdapter.ChampionStatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionStatusViewHolder =
        ChampionStatusViewHolder(ItemChampionStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionStatusViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 0
    class ChampionStatusViewHolder(val binding: ItemChampionStatusBinding) : RecyclerView.ViewHolder(binding.root)
}