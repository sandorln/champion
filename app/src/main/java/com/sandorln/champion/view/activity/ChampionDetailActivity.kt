package com.sandorln.champion.view.activity

import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionDetailBinding
import com.sandorln.champion.view.base.BaseActivity

class ChampionDetailActivity : BaseActivity<ActivityChampionDetailBinding>(R.layout.activity_champion_detail) {
    override suspend fun initViewModelSetting() {
    }

    override suspend fun initObjectSetting() {
    }

    override suspend fun initViewSetting() {
        initAppbarHeight()
    }

    override suspend fun initObserverSetting() {
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