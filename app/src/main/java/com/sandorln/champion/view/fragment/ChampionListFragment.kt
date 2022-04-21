package com.sandorln.champion.view.fragment

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionListFragment : BaseFragment<FragmentChampionListBinding>(R.layout.fragment_champion_list) {
    /* viewModels */
    private val championViewModel: ChampionViewModel by viewModels()

    /* Adapters */
    private lateinit var championThumbnailAdapter: ChampionThumbnailAdapter

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var cleanBtnClick: View.OnTouchListener                    /* 검색 창 오른쪽 X 버튼 */

    override fun initObjectSetting() {
        championThumbnailAdapter = ChampionThumbnailAdapter {
            // 해당 챔피언의 상세 내용을 가져옴
            lifecycleScope.launchWhenResumed {
                when (val result = championViewModel.getChampionDetailInfo(it.id)) {
                    is ResultData.Success -> result.data?.let { champion ->
                        if (champion.name.isNotEmpty()) {
                            /* 검색 중 챔피언을 눌렀을 시 _ 키보드 및 검색창 닫기 */
                            if (binding.editSearchChamp.hasFocus()) {
                                binding.editSearchChamp.clearFocus()
                                inputMethodManager.hideSoftInputFromWindow(binding.editSearchChamp.windowToken, 0)
                            }
                        }

                        startActivity(ChampionDetailActivity.newIntent(champion, requireContext()))
                    }
                }
            }
        }

        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        cleanBtnClick = View.OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event!!.action == MotionEvent.ACTION_UP &&
                event.rawX >= (binding.editSearchChamp.right - binding.editSearchChamp.compoundDrawables[DRAWABLE_RIGHT].bounds.width())
            ) {
                binding.editSearchChamp.setText("")
                binding.editSearchChamp.clearFocus()
                inputMethodManager.hideSoftInputFromWindow(binding.editSearchChamp.windowToken, 0)
            }
            return@OnTouchListener false
        }
    }

    override fun initViewSetting() {
        binding.rvChampions.setHasFixedSize(true)
        binding.rvChampions.adapter = championThumbnailAdapter

        binding.editSearchChamp.doOnTextChanged { text, _, _, _ ->
            championViewModel.changeSearchChampionName(text.toString())
        }
        binding.editSearchChamp.onFocusChangeListener = View.OnFocusChangeListener { _, _ -> }
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    championViewModel
                        .championVersion
                        .collectLatest { championVersion ->
                            binding.tvVersion.text = "CHAMPION VERSION $championVersion"
                        }
                }
                launch {
                    championViewModel
                        .showChampionList
                        .collectLatest { result ->
                            binding.pbContent.isVisible = result is ResultData.Loading

                            val championList = when (result) {
                                is ResultData.Success -> result.data ?: mutableListOf()
                                is ResultData.Failed -> {
                                    Toast.makeText(requireContext(), result.exception.message, Toast.LENGTH_SHORT).show()
                                    result.data ?: mutableListOf()
                                }
                                else -> mutableListOf()
                            }

                            championThumbnailAdapter.submitList(championList) {
                                binding.rvChampions.scrollToPosition(0)
                            }
                        }
                }

                launch {
                    championViewModel
                        .searchChampionData
                        .collectLatest { search ->
                            with(binding.editSearchChamp) {
                                if (search.isNotEmpty()) {
                                    /* 검색어를 모두 지우는 아이콘 생성 */
                                    setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(), R.drawable.round_clear_white_18), null)
                                    setOnTouchListener(cleanBtnClick)
                                } else {
                                    /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                                    setOnTouchListener(null)
                                }
                            }
                        }
                }
            }
        }
    }
}