package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionThumbnailSkillBinding
import com.sandorln.model.ChampionData.ChampionSpell
import com.sandorln.model.type.SpellType
import com.sandorln.champion.view.adapter.diff.DiffUtils

class ChampionThumbnailSkillAdapter(var onChangeSkillType: (championSpell: ChampionSpell, spellType: com.sandorln.model.type.SpellType) -> Unit = { _, _ -> }) :
    ListAdapter<ChampionSpell, ChampionThumbnailSkillAdapter.ChampionThumbnailSkillViewHolder>(DiffUtils.DIFF_CHAMPION_SKILL) {

    private var selectSpellType: com.sandorln.model.type.SpellType = com.sandorln.model.type.SpellType.P
    var championVersion = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionThumbnailSkillViewHolder =
        ChampionThumbnailSkillViewHolder(ItemChampionThumbnailSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionThumbnailSkillViewHolder, position: Int) {
        getItem(position)?.let { spell ->
            with(holder.binding) {
                imgChampionSkill.setSkillIcon(championVersion, spell.image.full, true)
                tvSkillType.text = ""
                vSelect.isVisible = true
            }

            holder.itemView.setOnClickListener {
//                if (selectSpellType != spellType) {
//                    selectSpellType = spellType
//                    onChangeSkillType(spell, spellType)
//                    notifyDataSetChanged()
//                }
            }
        }
    }

    class ChampionThumbnailSkillViewHolder(var binding: ItemChampionThumbnailSkillBinding) : RecyclerView.ViewHolder(binding.root)
}