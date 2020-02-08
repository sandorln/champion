package com.sandorln.champion.view.main

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sandorln.champion.R
import com.sandorln.champion.adapter.ChampAdapter
import com.sandorln.champion.adapter.ChampSkinAdapter
import com.sandorln.champion.api.data.CharacterData
import com.sandorln.champion.databinding.AMainBinding
import com.sandorln.champion.databinding.IncludeSmallChampInfoBinding
import com.sandorln.champion.view.BaseActivity
import com.sandorln.champion.viewmodel.ChampViewModel
import com.sandorln.champion.viewmodel.factory.ViewModelFactory

class MainActivity : BaseActivity<AMainBinding>() {
    override fun getLayout(): Int = R.layout.a_main

    lateinit var champInfoBinding: IncludeSmallChampInfoBinding

    val champViewModel: ChampViewModel by lazy { ViewModelProvider(this, ViewModelFactory(application))[ChampViewModel::class.java] }
    val inputMethodManager: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    /* Bottom Sheet */
    private lateinit var bottomSheet: BottomSheetBehavior<View>

    /* Champion List Adapter */
    lateinit var champAdapter: ChampAdapter
    lateinit var champSkinAdapter: ChampSkinAdapter

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

    override fun initBindingSetting() {
        binding.act = this
        binding.vm = champViewModel
    }

    override fun initObjectSetting() {
        champInfoBinding = DataBindingUtil.findBinding(binding.includeBottom.root)!!
        bottomSheet = BottomSheetBehavior.from(champInfoBinding.bottomSheet)

        champAdapter = ChampAdapter(mutableListOf()) {}
        champSkinAdapter = ChampSkinAdapter()
    }

    override fun initViewSetting() {
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

    override fun initObserverSetting() {
        champViewModel.characterAllList.observe(this, Observer<List<CharacterData>> { characterList ->
            champAdapter.championList = characterList
            champAdapter.notifyDataSetChanged()
        })

        champViewModel.searchChampName.observe(this,
            Observer<String> { searchChampName ->
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
//        champAdapter.selectChampion.observe(this,
//            Observer<CharacterData> { champion ->
//                if (champion.cName.isNotEmpty()) {
//
//                    /* 검색 중 챔피언을 눌렀을 시 _ 키보드 및 검색창 닫기 */
//                    if (aMainBinding.editxMain.hasFocus()) {
//                        aMainBinding.editxMain.clearFocus()
//                        inputMethodManager.hideSoftInputFromWindow(
//                            aMainBinding.editxMain.windowToken,
//                            0
//                        )
//                    }
//
//                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
//
//                    /* 해당 챔피언의 상세 정보가 없을 시 다시 불러옴 */
//                    if (champion.cSkins.isEmpty() || champion.cAllytips.isEmpty() || champion.cEnemytips.isEmpty()) {
//                        /* 비어있는 Character Data 전달하여서 초기화 */
//                        champSkinAdapter.characterData = CharacterData()
//                        champSkinAdapter.notifyDataSetChanged()
//
//                        /* 상세 정보를 가져오기- */
//                        mainViewModel.getChampionInfo(champion) {
//                            champSkinAdapter.characterData = it
//                            aMainBinding.includeBottom.vpChampSkin.setCurrentItem(0, true)
//                            champSkinAdapter.notifyDataSetChanged()
//                        }
//                    } else {
//                        champSkinAdapter.characterData = champion
//                        aMainBinding.includeBottom.vpChampSkin.setCurrentItem(0, true)
//                        champSkinAdapter.notifyDataSetChanged()
//                    }
//
//                } else {
//                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
//                }
//            })
    }

    override fun onBackPressed() {
        if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        else if (currentFocus != null)
            currentFocus?.clearFocus()
        else
            finish()
    }


}