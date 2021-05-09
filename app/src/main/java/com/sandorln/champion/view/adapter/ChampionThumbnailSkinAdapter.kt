package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionThumbnailSkinBinding
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.view.adapter.diff.DiffUtils
import com.sandorln.champion.view.binding.setChampionSkin

class ChampionThumbnailSkinAdapter(var championId: String = "0") :
    ListAdapter<ChampionSkin, ChampionThumbnailSkinAdapter.ChampionThumbnailSkinViewHolder>(DiffUtils.DIFF_CHAMPION_SKIN) {
    lateinit var selectItemListener: (selectPosition: Int) -> Unit
    var selectItemPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionThumbnailSkinViewHolder =
        ChampionThumbnailSkinViewHolder(ItemChampionThumbnailSkinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionThumbnailSkinViewHolder, position: Int) {
        val skin = getItem(position)
        holder.binding.imgChampionSkin.setChampionSkin(championId, skin.skNum)
        holder.itemView.setOnClickListener {
            if (::selectItemListener.isInitialized && selectItemPosition != position) {
                selectItemPosition = position
                selectItemListener(position)
                notifyDataSetChanged()
            }
        }
        holder.binding.vSelect.visibility =
            if (selectItemPosition == position)
                View.VISIBLE
            else
                View.GONE
    }

    class ChampionThumbnailSkinViewHolder(val binding: ItemChampionThumbnailSkinBinding) : RecyclerView.ViewHolder(binding.root)
}