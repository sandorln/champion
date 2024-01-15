package com.sandorln.champion.view.fragment

import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionDetailBinding
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.SpellType
import com.sandorln.champion.util.playChampionSkill
import com.sandorln.champion.util.removeBrFromHtml
import com.sandorln.champion.view.adapter.ChampionThumbnailSkillAdapter
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionDetailFragment : BaseFragment<FragmentChampionDetailBinding>(R.layout.fragment_champion_detail) {
    /* ViewModels */
    private val championDetailViewModel: ChampionDetailViewModel by viewModels()

    /* Adapters */
    private lateinit var championThumbnailSkillAdapter: ChampionThumbnailSkillAdapter

    private var skillExoPlayer: ExoPlayer? = null
    private var exoController: PlayerControlView? = null
    private var exoControllerVolume: ImageView? = null


    override fun initObjectSetting() {
        binding.championDetailVM = championDetailViewModel

        championThumbnailSkillAdapter = ChampionThumbnailSkillAdapter()

        skillExoPlayer = ExoPlayer.Builder(requireContext())
            .setRenderersFactory(DefaultRenderersFactory(requireContext()))
            .setMediaSourceFactory(DefaultMediaSourceFactory(requireContext()))
            .setTrackSelector(DefaultTrackSelector(requireContext()))
            .build()
            .apply {
                playWhenReady = false
                repeatMode = ExoPlayer.REPEAT_MODE_OFF
                addListener(object : Player.Listener {
                    override fun onVolumeChanged(volume: Float) {
                        exoControllerVolume?.isSelected = volume > 0
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        binding.layoutNoSkill.isVisible = playbackState == Player.STATE_IDLE
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        binding.layoutNoSkill.isVisible = false
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        binding.layoutNoSkill.isVisible = true
                    }
                })
            }
    }

    override fun initViewSetting() {
        initAppbarHeight()

        binding.imgBack.setOnClickListener { findNavController().navigateUp() }
        exoController = binding.exoPlayerSkill.findViewById(com.google.android.exoplayer2.R.id.exo_controller)
        exoControllerVolume = exoController?.findViewById(R.id.exo_volume)
        exoControllerVolume?.isSelected = true
        exoControllerVolume?.setOnClickListener {
            if (exoControllerVolume?.isSelected == true) {
                binding.exoPlayerSkill.player?.volume = 0f
                exoControllerVolume?.isSelected = false
            } else {
                binding.exoPlayerSkill.player?.volume = 1f
                exoControllerVolume?.isSelected = true
            }
        }

        /* 스킬 관련 */
        binding.exoPlayerSkill.player = skillExoPlayer
        binding.rvThumbnailSkill.adapter = championThumbnailSkillAdapter
    }

    override fun initObserverSetting() {
        championDetailViewModel.championData.observe(this, Observer { champion ->
            binding.imgChampionThumbnail.setChampionThumbnail(champion.version, champion.id)
            /* 능력치 관련 */
            val championCompareBundle = bundleOf(BundleKeys.CHAMPION_ID to champion.id, BundleKeys.CHAMPION_VERSION to champion.version)
            childFragmentManager
                .beginTransaction()
                .replace(binding.frgChampionStatusCompare.id, ChampionStatusCompareFragment::class.java, championCompareBundle)
                .commitNowAllowingStateLoss()

            val championId = String.format("%04d", champion.key)

            /* 스킬 관련 */
            val skillList = champion.spells.toMutableList()
            skillList.add(0, champion.passive)
            championThumbnailSkillAdapter.championVersion = champion.version
            championThumbnailSkillAdapter.submitList(skillList)
            championThumbnailSkillAdapter.onChangeSkillType = { championSpell, spellType ->
                selectChampionSkill(championId, spellType, championSpell)
            }

            /* 스킬 선택 초기화 */
            selectChampionSkill(championId, SpellType.P, skillList.first())
        })

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    championDetailViewModel
                        .isVideoAutoPlay
                        .collectLatest { isVideoAutoPlay ->
                            skillExoPlayer?.playWhenReady = isVideoAutoPlay
                        }
                }
            }
        }
    }

    /**
     * 16:9 비율로 Appbar Expand Height 값을 동적 처리
     */
    private fun initAppbarHeight() {
        val width = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            requireActivity().windowManager.currentWindowMetrics.bounds.width()
        } else
            requireActivity().windowManager.defaultDisplay.width

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
    private fun selectChampionSkill(championId: String, spellType: SpellType, championSpell: ChampionData.ChampionSpell) {
        if (!championDetailViewModel.isVideoAutoPlay.value)
            skillExoPlayer?.pause()

        skillExoPlayer?.playChampionSkill(championId, spellType)
        binding.tvSkillDescription.text = championSpell.description.removeBrFromHtml()
        binding.tvSpellName.text = championSpell.name
    }
}