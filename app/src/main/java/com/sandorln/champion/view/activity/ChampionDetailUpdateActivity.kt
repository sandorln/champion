package com.sandorln.champion.view.activity

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionDetailUpdateBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.ChampionSpell
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.util.playChampionSkill
import com.sandorln.champion.view.adapter.ChampionStatusAdapter
import com.sandorln.champion.view.adapter.ChampionThumbnailSkillAdapter
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.view.binding.setChampionSplash
import com.sandorln.champion.view.binding.setToolbarChampionThumbnail
import com.sandorln.champion.viewmodel.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChampionDetailUpdateActivity : BaseActivity<ActivityChampionDetailUpdateBinding>(R.layout.activity_champion_detail_update) {
    @Inject
    lateinit var versionManager: VersionManager

    /* ViewModels */
    private val championViewModel: ChampionViewModel by viewModels()

    /* Adapters */
    private lateinit var championStatusAdapter: ChampionStatusAdapter
    private lateinit var championThumbnailSkillAdapter: ChampionThumbnailSkillAdapter

    var skillExoPlayer: ExoPlayer? = null

    companion object {
        fun newIntent(championData: ChampionData, context: Context): Intent = Intent(context, ChampionDetailUpdateActivity::class.java).apply {
            putExtra(BundleKeys.CHAMPION_DATA, championData as Parcelable)
        }
    }

    override fun initObjectSetting() {
        championStatusAdapter = ChampionStatusAdapter()
        championThumbnailSkillAdapter = ChampionThumbnailSkillAdapter()

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
    }

    override fun initObserverSetting() {
        championViewModel.championData.observe(this, Observer { champion ->
            championStatusAdapter.championData = champion

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
        binding.tvSkillDescription.text = HtmlCompat.fromHtml(championSpell.description, FROM_HTML_MODE_LEGACY)
        binding.tvSpellName.text = championSpell.name
    }
}