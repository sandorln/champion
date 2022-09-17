package com.sandorln.champion.view.fragment

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionStatusCompareBinding
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionStatusCompareViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionStatusCompareFragment : BaseFragment<FragmentChampionStatusCompareBinding>(R.layout.fragment_champion_status_compare) {
    /* ViewModels */
    private val championStatusCompareViewModel: ChampionStatusCompareViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    championStatusCompareViewModel
                        .firstChampionStatus
                        .combine(championStatusCompareViewModel.secondChampionStatus) { first, second ->
                            childFragmentManager
                                .beginTransaction()
                                .replace(
                                    binding.frgOriginal.id, ChampionStatusFragment::class.java, bundleOf(
                                        BundleKeys.CHAMPION_ORIGIN_STATUS_KEY to first,
                                        BundleKeys.CHAMPION_OTHER_STATUS_KEY to second
                                    )
                                )
                                .replace(
                                    binding.frgOther.id, ChampionStatusFragment::class.java, bundleOf(
                                        BundleKeys.CHAMPION_ORIGIN_STATUS_KEY to second,
                                        BundleKeys.CHAMPION_OTHER_STATUS_KEY to first
                                    )
                                )
                                .commitNowAllowingStateLoss()
                        }.collect()
                }
            }
        }
    }
}