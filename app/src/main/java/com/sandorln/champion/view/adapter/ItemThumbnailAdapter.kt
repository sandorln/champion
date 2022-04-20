package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemItemThumbnailBinding
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.util.removeBrFromHtml
import com.sandorln.champion.view.adapter.diff.DiffUtils

class ItemThumbnailAdapter : ListAdapter<ItemData, ItemThumbnailAdapter.ItemThumbnailViewHolder>(DiffUtils.DIFF_ITEM_DATA) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemThumbnailViewHolder =
        ItemThumbnailViewHolder(ItemItemThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemThumbnailViewHolder, position: Int) {
        with(holder.binding) {
            try {
                val itemData = getItem(position)
                imgItemThumbnail.setItemThumbnail(itemData.id)
                tvItemGold.text = itemData.gold.total.toString()
                tvItemName.text = itemData.name.removeBrFromHtml()
            } catch (e: Exception) {

            }
        }
    }

    class ItemThumbnailViewHolder(val binding: ItemItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root)
}