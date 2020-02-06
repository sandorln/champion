package com.sandorln.champion.view.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sandorln.champion.R
import com.sandorln.champion.api.LolDataService
import com.sandorln.champion.data.CharacterData
import com.sandorln.champion.databinding.AMainBinding
import kotlinx.android.synthetic.main.item_champion_icon.view.*

class MainActivity : AppCompatActivity() {
    lateinit var aMainBinding: AMainBinding
    val mainViewModel: MainViewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }
    val inputMethodManager: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }


    /* Bottom Sheet */
    private lateinit var bottomSheet: BottomSheetBehavior<View>
    /* Champion List Adapter */
    val champAdapter = MainChampAdapter()
    val champSkinAdapter = MainChampSkinAdapter()

    private val cleanBtnClick = View.OnTouchListener { v, event ->
        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3

        if (event!!.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (aMainBinding.editxMain.right - aMainBinding.editxMain.compoundDrawables[DRAWABLE_RIGHT].bounds.width())){
                mainViewModel.searchChamp.value = ""
            }
        }
        return@OnTouchListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK

        aMainBinding = DataBindingUtil.setContentView(this, R.layout.a_main)
        aMainBinding.lifecycleOwner = this
        aMainBinding.act = this
        aMainBinding.vm = mainViewModel

        /* 챔피언 리스트 어뎁터 */
        with(aMainBinding.rvChampions) {
            layoutManager = GridLayoutManager(this@MainActivity, 6)
            adapter = champAdapter
        }

        /* 챔피언 스킨 어뎁터 */
        with(aMainBinding.includeBottom.vpChampSkin) {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = champSkinAdapter
        }

        bottomSheet = BottomSheetBehavior.from(aMainBinding.includeBottom.bottomSheet)

        mainViewModel.characterDefaultList.observe(this,
            Observer<List<CharacterData>> { characterList ->
                champAdapter.championList = characterList
                champAdapter.notifyDataSetChanged()
            })

        // 사용자가 챔피언을 선택했을 경우
        // 해당 챔피언의 상세 내용을 가져옴
        champAdapter.selectChampion.observe(this,
            Observer<CharacterData> { champion ->
                if (champion.cName.isNotEmpty()) {

                    /* 검색 중 챔피언을 눌렀을 시 _ 키보드 및 검색창 닫기 */
                    if (aMainBinding.editxMain.hasFocus()) {
                        aMainBinding.editxMain.clearFocus()
                        inputMethodManager.hideSoftInputFromWindow(aMainBinding.editxMain.windowToken, 0)
                    }

                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

                    /* 해당 챔피언의 상세 정보가 없을 시 다시 불러옴 */
                    if (champion.cSkins.isEmpty() || champion.cAllytips.isEmpty() || champion.cEnemytips.isEmpty()) {
                        /* 비어있는 Character Data 전달하여서 초기화 */
                        champSkinAdapter.characterData = CharacterData()
                        champSkinAdapter.notifyDataSetChanged()

                        /* 상세 정보를 가져오기- */
                        mainViewModel.getChampionInfo(champion) {
                            champSkinAdapter.characterData = it
                            aMainBinding.includeBottom.vpChampSkin.setCurrentItem(0, true)
                            champSkinAdapter.notifyDataSetChanged()
                        }
                    } else {
                        champSkinAdapter.characterData = champion
                        aMainBinding.includeBottom.vpChampSkin.setCurrentItem(0, true)
                        champSkinAdapter.notifyDataSetChanged()
                    }

                } else {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })

        mainViewModel.searchChamp.observe(this,
            Observer<String> { searchChampName ->
                /* 검색어가 비어있지 않을 경우 */
                if (searchChampName.trim().isNotEmpty()) {
                    champAdapter.championList = mainViewModel.characterDefaultList.value!!.filter { champ ->
                        /* 검색어 / 검색 대상 공백 제거 */
                        champ.cName.replace(" ", "").startsWith(searchChampName.replace(" ", ""))
                    }

                    /* 검색어를 모두 지우는 아이콘 생성 */
                    with(aMainBinding.editxMain) {
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, getDrawable(R.drawable.round_clear_white_18), null
                        )
                        setOnTouchListener(cleanBtnClick)
                    }

                } else {
                    /* 검색어가 존재하지 않을 시 기본 챔피언 리스트 출력 */
                    champAdapter.championList = mainViewModel.characterDefaultList.value!!
                    /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                    with(aMainBinding.editxMain) {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        setOnTouchListener(null)
                    }
                }

                champAdapter.notifyDataSetChanged()
            })


        aMainBinding.editxMain.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
            if (isFocus)
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onBackPressed() {
        if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        else if (currentFocus != null)
            currentFocus?.clearFocus()
        else
            finish()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllChampion()
    }
}