package com.sandorln.champion.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionDetailBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionData.ChampionSpell
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.util.playChampionSkill
import com.sandorln.champion.util.removeBrFromHtml
import com.sandorln.champion.util.setChampionSplash
import com.sandorln.champion.view.adapter.ChampionFullSkinAdapter
import com.sandorln.champion.view.adapter.ChampionThumbnailSkillAdapter
import com.sandorln.champion.view.adapter.ChampionTipAdapter
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ChampionDetailActivity : BaseActivity<ActivityChampionDetailBinding>(R.layout.activity_champion_detail) {
    /* ViewModels */
    private val championViewModel: ChampionViewModel by viewModels()

    /* Adapters */
    private lateinit var championThumbnailSkillAdapter: ChampionThumbnailSkillAdapter
    private lateinit var championFullSkinAdapter: ChampionFullSkinAdapter
    private lateinit var championTipAdapter: ChampionTipAdapter
    private lateinit var championEnemyTipAdapter: ChampionTipAdapter

    private var skillExoPlayer: ExoPlayer? = null

    companion object {
        fun newIntent(championData: ChampionData, context: Context): Intent = Intent(context, ChampionDetailActivity::class.java).apply {
            putExtra(BundleKeys.CHAMPION_DATA, championData as Parcelable)
        }
    }

    override fun initObjectSetting() {
        championThumbnailSkillAdapter = ChampionThumbnailSkillAdapter()
        championFullSkinAdapter = ChampionFullSkinAdapter()
        championTipAdapter = ChampionTipAdapter()
        championEnemyTipAdapter = ChampionTipAdapter()

        skillExoPlayer = ExoPlayer.Builder(this)
            .setRenderersFactory(DefaultRenderersFactory(this))
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
            .apply {
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        binding.pbLoadingSkill.isVisible = playbackState == Player.STATE_BUFFERING
                        binding.layoutNoSkill.isVisible = playbackState == Player.STATE_IDLE
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        binding.layoutNoSkill.isVisible = false
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        binding.layoutNoSkill.isVisible = true
                    }
                })
            }
    }

    override fun initViewSetting() {
        initAppbarHeight()

        binding.imgBack.setOnClickListener { finish() }

        /* 스킬 관련 */
        binding.exoPlayerSkill.player = skillExoPlayer
        binding.rvThumbnailSkill.adapter = championThumbnailSkillAdapter

        /* 스킨 관련 */
        binding.vpFullSkin.apply {
            adapter = championFullSkinAdapter

            /* 양옆 미리보기 처리 */
            val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.padding_xlarge)
            val pagerWidth = resources.getDimensionPixelOffset(R.dimen.padding_middle)

            setPageTransformer { page, position ->
                page.translationX = -(pageMarginPx + pagerWidth) * position
                page.scaleY = 1 - (0.10f * abs(position))
            }

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.right = pageMarginPx
                    outRect.left = pageMarginPx
                }
            })
        }

        binding.vVpFullSkinPre.setOnClickListener {
            try {
                val index = binding.vpFullSkin.currentItem
                if (index > 0)
                    binding.vpFullSkin.setCurrentItem(index - 1, true)
            } catch (e: Exception) {

            }
        }

        binding.vVpFullSkinNext.setOnClickListener {
            try {
                val index = binding.vpFullSkin.currentItem
                if (index < championFullSkinAdapter.currentList.size - 1)
                    binding.vpFullSkin.setCurrentItem(index + 1, true)
            } catch (e: Exception) {

            }
        }

        /* 팁 관련 */
        binding.rvAllyTips.adapter = championTipAdapter
        binding.rvEnemyTips.adapter = championEnemyTipAdapter
    }

    override fun initObserverSetting() {
        championViewModel.championData.observe(this, Observer { champion ->
            binding.imgChampionThumbnail.setChampionThumbnail(champion.id)
            binding.imgChampionSplash.setChampionSplash(champion.id, champion.skins.first().num)

            val championId = String.format("%04d", champion.key)

            /* 스토리 관련 */
            binding.tvChampionName.text = champion.name
            binding.tvChampionTitle.text = champion.title
            binding.tvChampionStory.text = champion.blurb

            /* 스킬 관련 */
            val skillList = champion.spells.toMutableList()
            skillList.add(0, champion.passive)
            championThumbnailSkillAdapter.submitList(skillList)
            championThumbnailSkillAdapter.onChangeSkillType = { championSpell, spellType ->
                selectChampionSkill(championId, spellType, championSpell)
            }

            /* 스킬 선택 초기화 */
            selectChampionSkill(championId, SpellType.P, skillList.first())

            /* 스킨 관련 */
            binding.vpFullSkin.offscreenPageLimit = champion.skins.size
            championFullSkinAdapter.championId = champion.id
            championFullSkinAdapter.submitList(champion.skins)
            /* 스킨 변경에 따른 상단 이름 및 썸네일 변경 */
            binding.vpFullSkin.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    try {
                        val viewHolder = (binding.vpFullSkin[0] as RecyclerView).findViewHolderForAdapterPosition(position) as ChampionFullSkinAdapter.ChampionFullSkinViewHolder
                        binding.imgChampionSplash.setImageDrawable(viewHolder.binding.imgChampionSkin.drawable)
                        binding.tvChampionName.text = if (position == 0) champion.name else champion.skins[position].name
                    } catch (e: Exception) {
                        binding.imgChampionSplash.setChampionSplash(champion.id, champion.skins.first().num)
                        binding.tvChampionName.text = champion.name
                    }
                }
            })

            /* 팁 관련 */
            if (champion.allytips.isNotEmpty()) {
                binding.layoutTips.isVisible = true
                championTipAdapter.tips = champion.allytips
                championTipAdapter.notifyDataSetChanged()
            }
            if (champion.enemytips.isNotEmpty()) {
                binding.layoutEnemyTips.isVisible = true
                championEnemyTipAdapter.tips = champion.enemytips
                championEnemyTipAdapter.notifyDataSetChanged()
            }
        })

    }

    /**
     * 16:9 비율로 Appbar Expand Height 값을 동적 처리
     */
    private fun initAppbarHeight() {
        val width = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else
            windowManager.defaultDisplay.width

        val layoutParams = binding.appbar.layoutParams
        layoutParams.height = width / 16 * 9
        binding.appbar.layoutParams = layoutParams
    }

    override fun onResume() {
        super.onResume()
        skillExoPlayer?.prepare()
    }

    override fun onStop() {
        super.onStop()
        skillExoPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        skillExoPlayer?.release()
        skillExoPlayer = null
    }

    /**
     * 챔피언 스킬 변경시 사용
     */
    private fun selectChampionSkill(championId: String, spellType: SpellType, championSpell: ChampionSpell) {
        skillExoPlayer?.playChampionSkill(championId, spellType)
        binding.tvSkillDescription.text = championSpell.description.removeBrFromHtml()
        binding.tvSpellName.text = championSpell.name
    }
}