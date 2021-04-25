package com.sandorln.champion.view.activity

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityMainBinding
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.view.adapter.ChampionThumbnailAdapter
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.ChampionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    @Inject
    lateinit var versionManager: VersionManager

    private val championViewModel: ChampionViewModel by viewModels()
    private val inputMethodManager: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    /* Champion List Adapter */
    private lateinit var championThumbnailAdapter: ChampionThumbnailAdapter

    /* 검색 창 오른쪽 X 버튼 */
    private val cleanBtnClick = View.OnTouchListener { v, event ->
        val DRAWABLE_RIGHT = 2

        if (event!!.action == MotionEvent.ACTION_UP &&
            event.rawX >= (binding.editSearchChamp.right - binding.editSearchChamp.compoundDrawables[DRAWABLE_RIGHT].bounds.width())
        )
            championViewModel.searchChampName.postValue("")

        return@OnTouchListener false
    }

    override suspend fun initObjectSetting() {
        championThumbnailAdapter = ChampionThumbnailAdapter(versionManager.getVersion().lvCategory.cvChampion) {
            // 사용자가 챔피언을 선택했을 경우
            // 해당 챔피언의 상세 내용을 가져옴
            lifecycleScope.launchWhenResumed {
                when (val result = championViewModel.getChampionDetailInfo(it.cId)) {
                    is ResultData.Success -> result.data?.let { champion ->
                        if (champion.cName.isNotEmpty()) {
                            /* 검색 중 챔피언을 눌렀을 시 _ 키보드 및 검색창 닫기 */
                            if (binding.editSearchChamp.hasFocus()) {
                                binding.editSearchChamp.clearFocus()
                                inputMethodManager.hideSoftInputFromWindow(binding.editSearchChamp.windowToken, 0)
                            }
                        }

                        startActivity(ChampionDetailActivity.newIntent(champion, this@MainActivity))
                    }
                }
            }
        }
    }

    override suspend fun initViewSetting() {
        /* 챔피언 리스트 어뎁터 */
        binding.rvChampions.adapter = championThumbnailAdapter

        /* 챔피언 스킨 어뎁터 */

        binding.editSearchChamp.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
        }

    }

    override suspend fun initObserverSetting() {
        binding.act = this
        binding.vm = championViewModel

        championViewModel.championAllList.observe(this, Observer { resultCharacterList ->
            when (resultCharacterList) {
                is ResultData.Success -> championThumbnailAdapter.submitList(resultCharacterList.data ?: mutableListOf())
                is ResultData.Failed -> {
                }
            }
        })

        championViewModel.searchChampName.observe(this, Observer { searchChampionName ->
            championViewModel.searchChampion(searchChampionName)

            /* 검색어가 비어있지 않을 경우 */
            if (searchChampionName.isNotEmpty()) {
                /* 검색어를 모두 지우는 아이콘 생성 */
                with(binding.editSearchChamp) {
                    setCompoundDrawablesWithIntrinsicBounds(
                        null, null, getDrawable(R.drawable.round_clear_white_18), null
                    )
                    setOnTouchListener(cleanBtnClick)
                }
            } else {
                /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                with(binding.editSearchChamp) {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    setOnTouchListener(null)
                }
            }
        })
    }

    override fun onBackPressed() {
        when {
            currentFocus != null -> currentFocus?.clearFocus()
            championViewModel.searchChampName.value!!.isNotEmpty() -> championViewModel.searchChampName.postValue("")
            else -> finish()
        }
    }
}