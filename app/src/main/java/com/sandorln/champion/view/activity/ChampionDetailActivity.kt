package com.sandorln.champion.view.activity

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionDetailBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.view.adapter.ChampionStatusAdapter
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
class ChampionDetailActivity : BaseActivity<ActivityChampionDetailBinding>(R.layout.activity_champion_detail) {
    @Inject
    lateinit var versionManager: VersionManager
    lateinit var championStatusAdapter: ChampionStatusAdapter

    private val championViewModel: ChampionViewModel by viewModels()


    companion object {
        fun newIntent(championData: ChampionData, context: Context): Intent = Intent(context, ChampionDetailActivity::class.java).apply {
            putExtra(BundleKeys.CHAMPION_DATA, championData as Parcelable)
        }
    }

    override suspend fun initObjectSetting() {
        championStatusAdapter = ChampionStatusAdapter()
    }

    override suspend fun initViewSetting() {
        initAppbarHeight()
        binding.rvChampionsStatus.adapter = championStatusAdapter
        binding.imgBack.setOnClickListener { finish() }
    }

    override suspend fun initObserverSetting() {
        championViewModel.championData.observe(this, Observer { champion ->
            championStatusAdapter.championData = champion

            binding.tvChampionName.text = champion.cName
            binding.tvChampionTitle.text = champion.cTitle
            binding.imgChampionSplash.setChampionSplash(champion)

            lifecycleScope.launchWhenResumed {
                binding.imgChampionThumbnail.setToolbarChampionThumbnail(versionManager.getVersion().lvCategory.cvChampion, champion.cId)
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
}