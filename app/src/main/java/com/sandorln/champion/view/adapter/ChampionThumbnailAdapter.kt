package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionIconBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.view.adapter.diff.DiffUtils

class ChampionThumbnailAdapter(
    var onClickItem: (selectChampion: ChampionData) -> Unit
) :
    ListAdapter<ChampionData, ChampionThumbnailAdapter.ThumbnailChampionViewHolder>(DiffUtils.DIFF_CHAMPION_DATA) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailChampionViewHolder =
        ThumbnailChampionViewHolder(ItemChampionIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ThumbnailChampionViewHolder, position: Int) {
        val championData = getItem(position)
        holder.itemView.setOnClickListener { onClickItem(championData) }

        with(holder.binding) {
            tvChampionName.text = championData.name
            imgChampionIcon.setChampionThumbnail(championData.version, championData.id)
        }
    }

    class ThumbnailChampionViewHolder(val binding: ItemChampionIconBinding) : RecyclerView.ViewHolder(binding.root)
}