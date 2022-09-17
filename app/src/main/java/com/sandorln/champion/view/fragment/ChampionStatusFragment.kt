package com.sandorln.champion.view.fragment

import androidx.fragment.app.viewModels
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionStatusBinding
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChampionStatusFragment : BaseFragment<FragmentChampionStatusBinding>(R.layout.fragment_champion_status) {
    /* ViewModels */
    private val championStatusViewModel: ChampionStatusViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
    }

    override fun initObserverSetting() {
    }
}