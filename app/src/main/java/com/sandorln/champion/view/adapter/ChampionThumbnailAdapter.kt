package com.sandorln.champion.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionIconBinding
import com.sandorln.champion.view.adapter.diff.DiffUtils
import com.sandorln.model.data.champion.ChampionData
import com.sandorln.model.data.champion.SummaryChampion

class ChampionThumbnailAdapter(
    var spriteImageMap: Map<String, Bitmap?> = emptyMap(),
    var onClickItem: (selectChampion: ChampionData) -> Unit
) :
    ListAdapter<SummaryChampion, ChampionThumbnailAdapter.ThumbnailChampionViewHolder>(DiffUtils.DIFF_SUMMARY_CHAMPION_DATA) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailChampionViewHolder =
        ThumbnailChampionViewHolder(ItemChampionIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ThumbnailChampionViewHolder, position: Int) {
        val championData = getItem(position)
//        holder.itemView.setOnClickListener { onClickItem(championData) }

        with(holder.binding) {
            tvChampionName.text = championData.name
            val championImageInfo = championData.image
            val spriteImage = spriteImageMap[championImageInfo.sprite]

            if (spriteImage == null) {
                imgChampionIcon.setImageIcon(null)
                return
            }

            val championIcon = Bitmap.createBitmap(spriteImage, championImageInfo.x, championImageInfo.y, championImageInfo.w, championImageInfo.h)
            imgChampionIcon.setImageBitmap(championIcon)
//            imgChampionIcon.setChampionThumbnail(championData.version, championData.id)
        }
    }

    class ThumbnailChampionViewHolder(val binding: ItemChampionIconBinding) : RecyclerView.ViewHolder(binding.root)
}