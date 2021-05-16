package com.sandorln.champion.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.sandorln.champion.databinding.ItemChampionMainBlurbBinding
import com.sandorln.champion.databinding.ItemChampionMainSkillBinding
import com.sandorln.champion.databinding.ItemChampionMainSkinListBinding
import com.sandorln.champion.databinding.ItemChampionMainTipsBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSkin
import com.sandorln.champion.model.ChampionSpell
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.model.type.TipsType
import com.sandorln.champion.view.adapter.listener.SnapScrollListener

class ChampionStatusAdapter(var championData: ChampionData = ChampionData()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        0 -> ChampionBlurbViewHolder(ItemChampionMainBlurbBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        1 -> ChampionSkillViewHolder(ItemChampionMainSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        2 -> ChampionSkinListViewHolder(ItemChampionMainSkinListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        3 -> ChampionTipsListViewHolder(ItemChampionMainTipsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> ChampionBlurbViewHolder(ItemChampionMainBlurbBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder) {
            when (this) {
                is ChampionBlurbViewHolder -> {
                    binding.tvTitle.text = "STORY"
                    binding.tvChampionBlurb.text = championData.cBlurb
                }
                is ChampionSkillViewHolder -> bind(championData)
                is ChampionSkinListViewHolder -> bind(championData.cId, championData.cName, championData.cSkins)
                is ChampionTipsListViewHolder -> bind(championData.cAllytips, championData.cEnemytips)
            }
        }
    }

    override fun getItemCount(): Int = 4

    class ChampionBlurbViewHolder(val binding: ItemChampionMainBlurbBinding) : RecyclerView.ViewHolder(binding.root)
    class ChampionSkinListViewHolder(val binding: ItemChampionMainSkinListBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var thumbnailAdapterFull: ChampionThumbnailSkinAdapter
        lateinit var fullAdapterFull: ChampionFullSkinAdapter

        fun bind(championId: String, defaultName: String, skinList: List<ChampionSkin>) {
            binding.tvTitle.text = "SKIN"

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

    class ChampionSkillViewHolder(val binding: ItemChampionMainSkillBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            var skillExoPlayer: SimpleExoPlayer? = null
        }

        lateinit var championId: String
        lateinit var championThumbnailSkillAdapter: ChampionThumbnailSkillAdapter
        fun bind(championData: ChampionData) {
            binding.tvTitle.text = "SKILL"

            championId = String.format("%04d", championData.cKey)

            skillExoPlayer = SimpleExoPlayer.Builder(binding.root.context).build()
            skillExoPlayer?.playWhenReady = true
            skillExoPlayer?.repeatMode = SimpleExoPlayer.REPEAT_MODE_ONE

            championThumbnailSkillAdapter = ChampionThumbnailSkillAdapter(::changeSpellView)
            binding.rvThumbnailSkill.adapter = championThumbnailSkillAdapter

            binding.exoPlayer.player = skillExoPlayer

            /* 초기화 */
            val spellList = championData.cSpellList.toMutableList()
            spellList.add(0, championData.cPassive)
            championThumbnailSkillAdapter.submitList(spellList)

            changeSpellView(championData.cPassive, SpellType.P)
        }

        private fun changeSpellView(championSpell: ChampionSpell, spellType: SpellType) {
            val mediaItem =
                if (spellType == SpellType.P)
                    MediaItem.fromUri(Uri.parse("https://d28xe8vt774jo5.cloudfront.net/champion-abilities/${championId}/ability_${championId}_${spellType.name}1.mp4"))
                else
                    MediaItem.fromUri(Uri.parse("https://d28xe8vt774jo5.cloudfront.net/champion-abilities/${championId}/ability_${championId}_${spellType.name}1.webm"))
            skillExoPlayer?.setMediaItem(mediaItem)
            skillExoPlayer?.prepare()

            binding.tvDescription.text = championSpell.description
            binding.tvSpellName.text = championSpell.name
        }
    }

    fun stopSkillExoPlayer() {
        ChampionSkillViewHolder.skillExoPlayer?.stop()
        ChampionSkillViewHolder.skillExoPlayer?.release()
        ChampionSkillViewHolder.skillExoPlayer = null
    }

    fun startSkillExoPlayer() {
        ChampionSkillViewHolder.skillExoPlayer?.prepare()
    }
}