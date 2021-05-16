package com.sandorln.champion.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.databinding.ItemChampionThumbnailSkillBinding
import com.sandorln.champion.model.ChampionSpell
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.view.adapter.diff.DiffUtils
import com.sandorln.champion.view.binding.setSkillIcon

class ChampionThumbnailSkillAdapter(private val onChangeSkillType: (championSpell: ChampionSpell, spellType: SpellType) -> Unit) :
    ListAdapter<ChampionSpell, ChampionThumbnailSkillAdapter.ChampionThumbnailSkillViewHolder>(DiffUtils.DIFF_CHAMPION_SKILL) {

    var selectSpellType = SpellType.P

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChampionThumbnailSkillViewHolder =
        ChampionThumbnailSkillViewHolder(ItemChampionThumbnailSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ChampionThumbnailSkillViewHolder, position: Int) {
        getItem(position)?.let { spell ->
            val spellType = spell.getSpellType(position)

            with(holder.binding) {
                imgChampionSkill.setSkillIcon(spell.image.imgFull, spellType == SpellType.P)
                tvSkillType.text = spellType.name
                vSelect.visibility = if (selectSpellType == spellType) View.VISIBLE else View.INVISIBLE
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