package com.sandorln.champion.view.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentSummonerSpellListBinding
import com.sandorln.model.result.ResultData
import com.sandorln.champion.view.adapter.SummonerSpellAdapter
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.SummonerSpellViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SummonerSpellListFragment : BaseFragment<FragmentSummonerSpellListBinding>(R.layout.fragment_summoner_spell_list) {
    /* ViewModels */
    private val summonerSpellViewModel: SummonerSpellViewModel by viewModels()

    /* Adapters */
    private lateinit var summonerSpellAdapter: SummonerSpellAdapter


    override fun initObjectSetting() {
        summonerSpellAdapter = SummonerSpellAdapter()
    }

    override fun initViewSetting() {
        binding.rvSummonerSpell.adapter = summonerSpellAdapter
        binding.error.retry = { summonerSpellViewModel.refreshSummonerSpellList() }
        binding.refreshSpell.setOnRefreshListener { summonerSpellViewModel.refreshSummonerSpellList() }
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    summonerSpellViewModel
                        .summonerSpellList
                        .collectLatest { result ->
                            binding.refreshSpell.isRefreshing = false
                            binding.pbContent.isVisible = result is com.sandorln.model.result.ResultData.Loading
                            binding.error.isVisible = result is com.sandorln.model.result.ResultData.Failed

                            val summonerSpellList = when (result) {
                                is com.sandorln.model.result.ResultData.Success -> result.data ?: mutableListOf()
                                is com.sandorln.model.result.ResultData.Failed -> {
                                    binding.error.errorMsg = result.exception.message ?: "오류 발생"
                                    result.data ?: mutableListOf()
                                }
                                else -> mutableListOf()
                            }
                            summonerSpellAdapter.submitList(summonerSpellList)
                        }
                }
            }
        }
    }
}