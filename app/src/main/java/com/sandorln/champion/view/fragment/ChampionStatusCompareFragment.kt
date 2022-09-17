package com.sandorln.champion.view.fragment

import androidx.fragment.app.viewModels
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionStatusCompareBinding
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionStatusCompareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChampionStatusCompareFragment : BaseFragment<FragmentChampionStatusCompareBinding>(R.layout.fragment_champion_status_compare) {
    /* ViewModels */
    private val championStatusCompareViewModel: ChampionStatusCompareViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
    }

    override fun initObserverSetting() {
    }
}