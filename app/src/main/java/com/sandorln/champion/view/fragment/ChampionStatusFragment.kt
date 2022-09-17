package com.sandorln.champion.view.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionStatusBinding
import com.sandorln.champion.view.adapter.ChampionStatusAdapter
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionStatusFragment : BaseFragment<FragmentChampionStatusBinding>(R.layout.fragment_champion_status) {
    /* ViewModels */
    private val championStatusViewModel: ChampionStatusViewModel by viewModels()

    /* Adapters */
    private lateinit var championStatusAdapter: ChampionStatusAdapter

    override fun initObjectSetting() {
        championStatusAdapter = ChampionStatusAdapter()
    }

    override fun initViewSetting() {
        binding.rvStatus.adapter = championStatusAdapter
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    championStatusViewModel
                        .originalChampionVersion
                        .collectLatest {
                            binding.tvVersion.titleName = it
                        }
                }
                launch {
                    championStatusViewModel
                        .originalChampionStatus
                        .combine(championStatusViewModel.otherChampionStatus) { original, other ->
                            championStatusAdapter.status = original
                            championStatusAdapter.otherStatus = other
                            championStatusAdapter.notifyDataSetChanged()
                        }
                        .collect()
                }
            }
        }
    }
}