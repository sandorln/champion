package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionIconBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.view.adapter.diff.DiffUtils
import com.sandorln.champion.view.binding.setChampionThumbnail

class ThumbnailChampionAdapter(
    var championVersion: String = "",
    var onClickItem: (selectChampion: ChampionData) -> Unit
) :
    ListAdapter<ChampionData, ThumbnailChampionAdapter.ThumbnailChampionViewHolder>(DiffUtils.DIFF_CHAMPION_DATA) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailChampionViewHolder =
        ThumbnailChampionViewHolder(ItemChampionIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ThumbnailChampionViewHolder, position: Int) {
        val championData = getItem(position)
        holder.itemView.setOnClickListener { onClickItem(championData) }

        with(holder.binding) {
            tvChampionName.text = championData.cName
            imgChampionIcon.setChampionThumbnail(championVersion, championData.cId)
        }
    }

    class ThumbnailChampionViewHolder(val binding: ItemChampionIconBinding) : RecyclerView.ViewHolder(binding.root)
}