package com.sandorln.champion.view.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentSummonerSpellListBinding
import com.sandorln.champion.model.result.ResultData
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
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    summonerSpellViewModel
                        .getSummonerSpellList
                        .collectLatest { result ->
                            when (result) {
                                is ResultData.Success -> summonerSpellAdapter.submitList(result.data)
                                is ResultData.Failed -> {

                                }
                                else -> {

                                }
                            }
                        }
                }
            }
        }
    }
}