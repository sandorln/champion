package com.sandorln.champion.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionDetailBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSpell
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.util.fromHtml
import com.sandorln.champion.util.playChampionSkill
import com.sandorln.champion.util.setChampionSplash
import com.sandorln.champion.util.setToolbarChampionThumbnail
import com.sandorln.champion.view.adapter.ChampionFullSkinAdapter
import com.sandorln.champion.view.adapter.ChampionThumbnailSkillAdapter
import com.sandorln.champion.view.adapter.ChampionTipAdapter
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import kotlin.math.abs

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChampionDetailActivity : BaseActivity<ActivityChampionDetailBinding>(R.layout.activity_champion_detail) {
    @Inject
    lateinit var versionManager: VersionManager

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
        skillExoPlayer?.playWhenReady = true
        skillExoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
    }

    override fun initViewSetting() {
        initAppbarHeight()

        binding.imgBack.setOnClickListener { finish() }
        binding.exoPlayerSkill.player = skillExoPlayer
        binding.rvThumbnailSkill.adapter = championThumbnailSkillAdapter
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
        binding.rvAllyTips.adapter = championTipAdapter
        binding.rvEnemyTips.adapter = championEnemyTipAdapter
    }

    override fun initObserverSetting() {
        championViewModel.championData.observe(this, Observer { champion ->
            lifecycleScope.launchWhenResumed {
                binding.imgChampionThumbnail.setToolbarChampionThumbnail(versionManager.getVersion().lvCategory.cvChampion, champion.cId)
            }

            val championId = String.format("%04d", champion.cKey)

            /* 스토리 관련 */
            binding.tvChampionName.text = champion.cName
            binding.tvChampionTitle.text = champion.cTitle
            binding.imgChampionSplash.setChampionSplash(champion)
            binding.tvChampionStory.text = champion.cBlurb

            /* 스킬 관련 */
            val skillList = champion.cSpellList.toMutableList()
            skillList.add(0, champion.cPassive)
            championThumbnailSkillAdapter.submitList(skillList)
            championThumbnailSkillAdapter.onChangeSkillType = { championSpell, spellType ->
                selectChampionSkill(championId, spellType, championSpell)
            }

            /* 스킬 선택 초기화 */
            selectChampionSkill(championId, SpellType.P, skillList.first())

            /* 스킨 관련 */
            binding.vpFullSkin.offscreenPageLimit = champion.cSkins.size
            championFullSkinAdapter.championId = champion.cId
            championFullSkinAdapter.submitList(champion.cSkins)

            /* 팁 관련 */
            if (champion.cAllytips.isNotEmpty()) {
                binding.layoutTips.isVisible = true
                championTipAdapter.tips = champion.cAllytips
                championTipAdapter.notifyDataSetChanged()
            }
            if (champion.cEnemytips.isNotEmpty()) {
                binding.layoutEnemyTips.isVisible = true
                championEnemyTipAdapter.tips = champion.cEnemytips
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
        binding.tvSkillDescription.text = championSpell.description.fromHtml()
        binding.tvSpellName.text = championSpell.name
    }
}