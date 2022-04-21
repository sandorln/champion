package com.sandorln.champion.view.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentSummonerSpellListBinding
import com.sandorln.champion.model.SummonerSpell
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.SummonerSpellViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SummonerSpellListFragment : BaseFragment<FragmentSummonerSpellListBinding>(R.layout.fragment_summoner_spell_list) {
    /* ViewModels */
    private val summonerSpellViewModel: SummonerSpellViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    summonerSpellViewModel
                        .getSummonerSpellList
                        .collectLatest { result ->
                            val resultData = (result as? ResultData.Success)?.data ?: mutableListOf()
                            binding.horizonSummonerSpell.setSummonerSpellList(resultData) {
                                summonerSpellViewModel.selectedSummonerSpell(it)
                            }

                            if (resultData.isNotEmpty())
                                summonerSpellViewModel.selectedSummonerSpell(resultData.first())
                        }
                }
                launch {
                    summonerSpellViewModel
                        .summonerSpell
                        .distinctUntilChangedBy { it.id }
                        .collectLatest { summonerSpell: SummonerSpell ->
                            with(binding) {
                                imgSummonerSpell.setSummonerSpellThumbnail(summonerSpell.version, summonerSpell.id)
                                tvCoolDown.text = "${summonerSpell.cooldownBurn} 초"
                                tvDescription.text = summonerSpell.description
                                tvName.text = summonerSpell.name
                            }
                        }
                }
            }
        }
    }
}