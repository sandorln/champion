package com.sandorln.champion.view.activity

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    /* viewModels */
    private val championViewModel: ChampionViewModel by viewModels()

    /* Adapters */
    private lateinit var championThumbnailAdapter: ChampionThumbnailAdapter

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var cleanBtnClick: View.OnTouchListener                    /* 검색 창 오른쪽 X 버튼 */

    override fun initObjectSetting() {
        championThumbnailAdapter = ChampionThumbnailAdapter {
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

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        binding.rvChampions.adapter = championThumbnailAdapter
        binding.editSearchChamp.doOnTextChanged { text, _, _, _ ->
            championViewModel.changeSearchChampionName(text.toString())

            with(binding.editSearchChamp) {
                if (text?.isNotEmpty() == true) {
                    /* 검색어를 모두 지우는 아이콘 생성 */
                    setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this@MainActivity, R.drawable.round_clear_white_18), null)
                    setOnTouchListener(cleanBtnClick)
                } else {
                    /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    setOnTouchListener(null)
                }
            }
        }
        binding.editSearchChamp.onFocusChangeListener = View.OnFocusChangeListener { _, _ -> }
        binding.tvVersion.text = "VERSION ${VersionManager.getVersion(this).lvTotalVersion}"
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                championViewModel.showChampionList.collect { result ->
                    when (result) {
                        is ResultData.Success -> {
                            binding.pbContent.isVisible = false
                            championThumbnailAdapter.submitList(result.data)
                        }
                        is ResultData.Loading -> {
                            binding.pbContent.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            binding.editSearchChamp.text?.toString()?.isNotEmpty() == true -> binding.editSearchChamp.setText("")
            else -> {
                moveTaskToBack(true)
                finishAndRemoveTask()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }
}