package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionBlurbBinding
import com.sandorln.champion.databinding.ItemChampionSkinListBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSkin

class ChampionStatusAdapter(var championData: ChampionData = ChampionData()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int = when (position) {
        1 -> 1
        else -> 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        0 -> ChampionBlurbViewHolder(ItemChampionBlurbBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        1 -> ChampionSkinListViewHolder(ItemChampionSkinListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw Exception("Not Find View Holder")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder) {
            when (this) {
                is ChampionBlurbViewHolder -> binding.tvChampionBlurb.text = championData.cBlurb
                is ChampionSkinListViewHolder -> bind(championData.cId, championData.cSkins)
            }
        }
    }

    override fun getItemCount(): Int = 5

    class ChampionBlurbViewHolder(val binding: ItemChampionBlurbBinding) : RecyclerView.ViewHolder(binding.root)
    class ChampionSkinListViewHolder(val binding: ItemChampionSkinListBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var thumbnailAdapter: ChampionSkinViewPagerAdapter
        lateinit var fullAdapter: ChampionSkinViewPagerAdapter

        fun bind(championId: String, skinList: List<ChampionSkin>) {
            thumbnailAdapter = ChampionSkinViewPagerAdapter(championId, skinList)
            fullAdapter = ChampionSkinViewPagerAdapter(championId, skinList)

            PagerSnapHelper().apply { attachToRecyclerView(binding.vpThumbnailSkin) }
            PagerSnapHelper().apply { attachToRecyclerView(binding.vpFullSkin) }

            binding.vpThumbnailSkin.adapter = thumbnailAdapter
            binding.vpFullSkin.adapter = fullAdapter
        }
    }
}