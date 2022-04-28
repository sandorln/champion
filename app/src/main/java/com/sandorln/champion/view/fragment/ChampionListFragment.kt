package com.sandorln.champion.view.fragment

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionListBinding
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.view.activity.ChampionDetailActivity
import com.sandorln.champion.view.adapter.ChampionThumbnailAdapter
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionListFragment : BaseFragment<FragmentChampionListBinding>(R.layout.fragment_champion_list) {
    /* viewModels */
    private val championViewModel: ChampionViewModel by viewModels()

    /* Adapters */
    private lateinit var championThumbnailAdapter: ChampionThumbnailAdapter

    private lateinit var inputMethodManager: InputMethodManager

    override fun initObjectSetting() {
        championThumbnailAdapter = ChampionThumbnailAdapter {
            // 해당 챔피언의 상세 내용을 가져옴
            lifecycleScope.launchWhenResumed {
                championViewModel.getChampionDetailInfo(it.version, it.id).firstOrNull { resultData ->
                    val isLoading = resultData is ResultData.Loading
                    binding.pbContent.isVisible = isLoading

                    when (resultData) {
                        is ResultData.Success -> startActivity(ChampionDetailActivity.newIntent(resultData.data ?: throw Exception(""), requireContext()))
                        is ResultData.Failed -> Toast.makeText(requireContext(), "오류 발생 ${resultData.exception}", Toast.LENGTH_SHORT).show()
                    }

                    !isLoading
                }
            }
        }

        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun initViewSetting() {
        with(binding.rvChampions) {
            setHasFixedSize(true)
            adapter = championThumbnailAdapter
        }

        binding.error.retry = { championViewModel.refreshChampionList() }
        binding.refreshChampion.setOnRefreshListener { championViewModel.refreshChampionList() }
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    championViewModel
                        .showChampionList
                        .collectLatest { result ->
                            binding.refreshChampion.isRefreshing = false
                            binding.pbContent.isVisible = result is ResultData.Loading
                            binding.error.isVisible = result is ResultData.Failed

                            val championList = when (result) {
                                is ResultData.Success -> result.data ?: mutableListOf()
                                is ResultData.Failed -> {
                                    binding.error.errorMsg = result.exception.message ?: "오류 발생"
                                    result.data ?: mutableListOf()
                                }
                                else -> mutableListOf()
                            }

                            championThumbnailAdapter.submitList(championList)
                        }
                }

                launch {
                    binding
                        .searchBar
                        .inputTextFlow
                        .collectLatest { search -> championViewModel.changeSearchChampionName(search) }
                }
            }
        }
    }
}