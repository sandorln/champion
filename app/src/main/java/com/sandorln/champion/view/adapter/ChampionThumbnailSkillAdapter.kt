package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionThumbnailSkillBinding
import com.sandorln.champion.model.ChampionSpell
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.util.setSkillIcon
import com.sandorln.champion.view.adapter.diff.DiffUtils

class ChampionThumbnailSkillAdapter(var onChangeSkillType: (championSpell: ChampionSpell, spellType: SpellType) -> Unit = { _, _ -> }) :
    ListAdapter<ChampionSpell, ChampionThumbnailSkillAdapter.ChampionThumbnailSkillViewHolder>(DiffUtils.DIFF_CHAMPION_SKILL) {

    private var selectSpellType: SpellType = SpellType.P

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionThumbnailSkillViewHolder =
        ChampionThumbnailSkillViewHolder(ItemChampionThumbnailSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionThumbnailSkillViewHolder, position: Int) {
        getItem(position)?.let { spell ->
            val spellType = spell.getSpellType(position)

            with(holder.binding) {
                imgChampionSkill.setSkillIcon(spell.image.full, spellType == SpellType.P)
                tvSkillType.text = spellType.name
                vSelect.isVisible = selectSpellType == spellType
            }

            holder.itemView.setOnClickListener {
                if (selectSpellType != spellType) {
                    selectSpellType = spellType
                    onChangeSkillType(spell, spellType)
                    notifyDataSetChanged()
                }
            }
        }
    }

    class ChampionThumbnailSkillViewHolder(var binding: ItemChampionThumbnailSkillBinding) : RecyclerView.ViewHolder(binding.root)
}