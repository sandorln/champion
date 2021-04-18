package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ItemChampionIconBinding
import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.view.binding.setCharacterThumbnail

class ChampAdapter(
    var championList: List<CharacterData>,
    var championVersion: String = "",
    var onClickItem: (selectChampion: CharacterData) -> Unit
) :
    RecyclerView.Adapter<ChampAdapter.MainChampionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainChampionViewHolder =
        MainChampionViewHolder(ItemChampionIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            .apply {
                val layoutParams = itemView.layoutParams
                layoutParams.height = (parent.width / 6.0).toInt()
                itemView.layoutParams = layoutParams
            }

    override fun getItemCount(): Int = championList.size

    override fun onBindViewHolder(holder: MainChampionViewHolder, position: Int) {
        val characterData = championList[position]

        holder.itemView.setOnClickListener { onClickItem(characterData) }

        with(holder.binding) {
            tvChampionName.text = characterData.cName
            imgChampionIcon.setCharacterThumbnail(championVersion, characterData.cId)
        }
    }

    class MainChampionViewHolder(val binding: ItemChampionIconBinding) : RecyclerView.ViewHolder(binding.root)
}