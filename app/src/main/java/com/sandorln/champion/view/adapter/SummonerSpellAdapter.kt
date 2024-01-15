package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemSummonerSpellBinding
import com.sandorln.model.SummonerSpell
import com.sandorln.champion.view.adapter.diff.DiffUtils

class SummonerSpellAdapter : ListAdapter<com.sandorln.model.SummonerSpell, SummonerSpellAdapter.SummonerSpellViewHolder>(DiffUtils.DIFF_SUMMONER_SPELL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummonerSpellViewHolder =
        SummonerSpellViewHolder(ItemSummonerSpellBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SummonerSpellViewHolder, position: Int) {
        with(holder.binding) {
            try {
                val summonerSpell = getItem(position)
                tvName.text = summonerSpell.name
                tvCoolDown.text = "${summonerSpell.cooldownBurn} 초"
                tvDescription.text = summonerSpell.description
                imgSummonerSpell.setSummonerSpellThumbnail(summonerSpell.version, summonerSpell.id)
            } catch (e: Exception) {

            }
        }
    }

    class SummonerSpellViewHolder(val binding: ItemSummonerSpellBinding) : RecyclerView.ViewHolder(binding.root)
}