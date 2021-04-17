package com.sandorln.champion.view.activity

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sandorln.champion.R
import com.sandorln.champion.view.adapter.ChampAdapter
import com.sandorln.champion.view.adapter.ChampSkinAdapter
import com.sandorln.champion.databinding.ActivityMainBinding
import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.databinding.BottomSheetChampInfoBinding
import com.sandorln.champion.view.base.BaseActivity
import com.sandorln.champion.viewmodel.ChampViewModel
import com.sandorln.champion.viewmodel.factory.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var champInfoBinding: BottomSheetChampInfoBinding

    private val champViewModel: ChampViewModel by lazy { ViewModelProvider(this, ViewModelFactory(application))[ChampViewModel::class.java] }
    private val inputMethodManager: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    /* Bottom Sheet */
    private lateinit var bottomSheet: BottomSheetBehavior<View>

    /* Champion List Adapter */
    private lateinit var champAdapter: ChampAdapter
    private lateinit var champSkinAdapter: ChampSkinAdapter

    /* 검색 창 오른쪽 X 버튼 */
    private val cleanBtnClick = View.OnTouchListener { v, event ->
        val DRAWABLE_RIGHT = 2

        if (event!!.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (binding.editSearchChamp.right - binding.editSearchChamp.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                champViewModel.searchChampName.value = ""
            }
        }
        return@OnTouchListener false
    }

    override suspend fun initViewModelSetting() {
        binding.act = this
        binding.vm = champViewModel
        champInfoBinding = DataBindingUtil.findBinding(binding.includeBottom.root)!!
        champInfoBinding.clickListener = View.OnClickListener { }
        champInfoBinding.lifecycleOwner = this
        champInfoBinding.champViewModel = champViewModel
    }

    override suspend fun initObjectSetting() {
        bottomSheet = BottomSheetBehavior.from(champInfoBinding.bottomSheet)

        champAdapter = ChampAdapter(mutableListOf()) {
            champViewModel.getChampionDetailInfo(it)
        }

        champSkinAdapter = ChampSkinAdapter()
    }

    override suspend fun initViewSetting() {
        /* 챔피언 리스트 어뎁터 */
        binding.rvChampions.adapter = champAdapter

        /* 챔피언 스킨 어뎁터 */
        champInfoBinding.vpChampSkin.adapter = champSkinAdapter

        binding.editSearchChamp.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
            if (isFocus)
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override suspend fun initObserverSetting() {
        champViewModel.errorMsg.observe(this, Observer { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                AlertDialog
                    .Builder(this)
                    .setTitle("Error")
                    .setMessage(errorMsg)
                    .setPositiveButton("Okay") { _, _ -> }
                    .create()
                    .show()
            }
        })
        

        champViewModel.characterAllList.observe(this, Observer { characterList ->
            champAdapter.championList = characterList
            champAdapter.notifyDataSetChanged()
        })

        champViewModel.searchChampName.observe(this, Observer { searchChampName ->
            /* 검색어가 비어있지 않을 경우 */
            if (searchChampName.trim().isNotEmpty()) {
                champAdapter.championList =
                    champViewModel.characterAllList.value!!.filter { champ ->
                        /* 검색어 / 검색 대상 공백 제거 */
                        champ.cName.replace(" ", "")
                            .startsWith(searchChampName.replace(" ", ""))
                    }

                /* 검색어를 모두 지우는 아이콘 생성 */
                with(binding.editSearchChamp) {
                    setCompoundDrawablesWithIntrinsicBounds(
                        null, null, getDrawable(R.drawable.round_clear_white_18), null
                    )
                    setOnTouchListener(cleanBtnClick)
                }

            } else {
                /* 검색어가 존재하지 않을 시 기본 챔피언 리스트 출력 */
                champAdapter.championList = champViewModel.characterAllList.value ?: mutableListOf()
                /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                with(binding.editSearchChamp) {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    setOnTouchListener(null)
                }
            }

            champAdapter.notifyDataSetChanged()
        })

        // 사용자가 챔피언을 선택했을 경우
        // 해당 챔피언의 상세 내용을 가져옴
        champViewModel.selectCharacter.observe(this, Observer<CharacterData> { champion ->
            if (champion.cName.isNotEmpty()) {
                /* 검색 중 챔피언을 눌렀을 시 _ 키보드 및 검색창 닫기 */
                if (binding.editSearchChamp.hasFocus()) {
                    binding.editSearchChamp.clearFocus()
                    inputMethodManager.hideSoftInputFromWindow(binding.editSearchChamp.windowToken, 0)
                }

                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

                /* 해당 챔피언의 상세 정보가 없을 시 다시 불러옴 */
                champSkinAdapter.characterData = champion
                champInfoBinding.vpChampSkin.setCurrentItem(0, true)
                champSkinAdapter.notifyDataSetChanged()

            } else {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
    }

    override fun onBackPressed() {
        when {
            bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            currentFocus != null -> currentFocus?.clearFocus()
            champViewModel.searchChampName.value!!.isNotEmpty() -> champViewModel.searchChampName.postValue("")
            else -> finish()
        }
    }
}