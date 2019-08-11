package com.sandorln.champion.view.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.sandorln.champion.R
import com.sandorln.champion.data.CharacterData
import com.sandorln.champion.databinding.AMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    lateinit var aMainBinding: AMainBinding
    val mainViewModel: MainViewModel by lazy { ViewModelProviders.of(this)[MainViewModel::class.java] }
    private val champAdapter = MainChampAdapter()

    private val cleanBtnClick = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3

            if (event!!.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (aMainBinding.editAppbar.right - aMainBinding.editAppbar.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    mainViewModel.searchChamp.value = ""
                    return true
                }
            }
            return false

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK

        aMainBinding = DataBindingUtil.setContentView(this, R.layout.a_main)
        aMainBinding.lifecycleOwner = this
        aMainBinding.act = this
        aMainBinding.vm = mainViewModel

        aMainBinding.rvChampions.layoutManager = GridLayoutManager(this, 6)
        aMainBinding.rvChampions.adapter = champAdapter

        setSupportActionBar(aMainBinding.toolbar)

        mainViewModel.characterDefaultList.observe(this,
            Observer<List<CharacterData>> { characterList ->
                champAdapter.championList = characterList
                champAdapter.notifyDataSetChanged()
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
                    with(aMainBinding.editAppbar) {
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, getDrawable(R.drawable.round_clear_white_18), null
                        )
                        setOnTouchListener(cleanBtnClick)
                    }

                    /* 검색어를 모두 지우는 아이콘 생성 */
                    with(aMainBinding.editToolbar) {
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, getDrawable(R.drawable.round_clear_white_18), null
                        )
                        setOnTouchListener(cleanBtnClick)
                    }

                } else {
                    /* 검색어가 존재하지 않을 시 기본 챔피언 리스트 출력 */
                    champAdapter.championList = mainViewModel.characterDefaultList.value!!

                    /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                    with(aMainBinding.editAppbar) {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        setOnTouchListener(null)
                    }

                    /* 검색어가 존재하지 않을 시 검색어를 모두 지우는 아이콘 삭제 */
                    with(aMainBinding.editToolbar) {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        setOnTouchListener(null)
                    }
                }

                champAdapter.notifyDataSetChanged()
            })

        /* 스크롤에 따른 이벤트 저장 */
        aMainBinding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (appBarLayout.totalScrollRange == 0 || verticalOffset == 0) {
                    aMainBinding.txlayoutInAppbar.visibility = View.VISIBLE
                    aMainBinding.txlayoutInToolbar.visibility = View.INVISIBLE
                } else if (abs(verticalOffset) >= (appBarLayout.totalScrollRange - aMainBinding.toolbar.height)) {
                    aMainBinding.txlayoutInAppbar.visibility = View.INVISIBLE
                    aMainBinding.txlayoutInToolbar.visibility = View.VISIBLE
                }
            })
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllChampion()
    }
}