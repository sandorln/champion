package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionBlurbBinding
import com.sandorln.champion.databinding.ItemChampionSkinListBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.view.adapter.listener.SnapScrollListener

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
                is ChampionSkinListViewHolder -> bind(championData.cId, championData.cName, championData.cSkins)
            }
        }
    }

    override fun getItemCount(): Int = 5

    class ChampionBlurbViewHolder(val binding: ItemChampionBlurbBinding) : RecyclerView.ViewHolder(binding.root)
    class ChampionSkinListViewHolder(val binding: ItemChampionSkinListBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var thumbnailAdapterFull: ChampionThumbnailSkinAdapter
        lateinit var fullAdapterFull: ChampionFullSkinAdapter

        fun bind(championId: String, defaultName: String, skinList: List<ChampionSkin>) {
            thumbnailAdapterFull = ChampionThumbnailSkinAdapter(championId)
            thumbnailAdapterFull.submitList(skinList)

            fullAdapterFull = ChampionFullSkinAdapter(championId)
            fullAdapterFull.submitList(skinList)

            val fullSnapHelper = PagerSnapHelper().apply {
                attachToRecyclerView(binding.rvFullSkin)
            }

            thumbnailAdapterFull.selectItemListener = { selectPosition ->
                binding.rvFullSkin.smoothScrollToPosition(selectPosition)
            }

            val fullSnapScrollListener = SnapScrollListener(fullSnapHelper) { snapPosition ->
                if (thumbnailAdapterFull.selectItemPosition != snapPosition) {
                    thumbnailAdapterFull.selectItemPosition = snapPosition
                    thumbnailAdapterFull.notifyDataSetChanged()
                }

                binding.rvThumbnailSkin.smoothScrollToPosition(snapPosition)
                binding.tvSkinName.text = if (snapPosition == 0) defaultName else skinList[snapPosition].skName
            }

            binding.rvThumbnailSkin.adapter = thumbnailAdapterFull

            binding.rvFullSkin.adapter = fullAdapterFull
            binding.rvFullSkin.addOnScrollListener(fullSnapScrollListener)
        }
    }
}