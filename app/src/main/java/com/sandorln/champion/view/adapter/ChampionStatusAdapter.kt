package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionMainBlurbBinding
import com.sandorln.champion.databinding.ItemChampionMainSkinListBinding
import com.sandorln.champion.databinding.ItemChampionMainTipsBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.model.type.TipsType
import com.sandorln.champion.view.adapter.listener.SnapScrollListener

class ChampionStatusAdapter(var championData: ChampionData = ChampionData()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        0 -> ChampionBlurbViewHolder(ItemChampionMainBlurbBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        1 -> ChampionTipsListViewHolder(ItemChampionMainTipsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        2 -> ChampionSkinListViewHolder(ItemChampionMainSkinListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> ChampionBlurbViewHolder(ItemChampionMainBlurbBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder) {
            when (this) {
                is ChampionBlurbViewHolder -> binding.tvChampionBlurb.text = championData.cBlurb
                is ChampionSkinListViewHolder -> bind(championData.cId, championData.cName, championData.cSkins)
                is ChampionTipsListViewHolder -> bind(championData.cAllytips, championData.cEnemytips)
            }
        }
    }

    override fun getItemCount(): Int = 5

    class ChampionBlurbViewHolder(val binding: ItemChampionMainBlurbBinding) : RecyclerView.ViewHolder(binding.root)
    class ChampionSkinListViewHolder(val binding: ItemChampionMainSkinListBinding) : RecyclerView.ViewHolder(binding.root) {
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
            binding.tvSkinName.text = defaultName
        }
    }

    class ChampionTipsListViewHolder(val binding: ItemChampionMainTipsBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var allyTipsAdapter: ChampionTipsAdapter
        lateinit var enemyTipsAdapter: ChampionTipsAdapter
        fun bind(allyTips: List<String>, enemyTips: List<String>) {
            allyTipsAdapter = ChampionTipsAdapter(allyTips, TipsType.ALLY)
            enemyTipsAdapter = ChampionTipsAdapter(enemyTips, TipsType.ENEMY)

            binding.rvAllyTips.adapter = allyTipsAdapter
            binding.rvEnemyTips.adapter = enemyTipsAdapter
        }
    }
}