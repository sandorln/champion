package com.sandorln.champion.view.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
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
    val champAdapter = MainChampAdapter()

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
                if (searchChampName.trim().isNotEmpty())
                    champAdapter.championList = mainViewModel.characterDefaultList.value!!.filter { champ ->
                        champ.cName.startsWith(searchChampName)
                    }
                else
                    champAdapter.championList = mainViewModel.characterDefaultList.value!!

                champAdapter.notifyDataSetChanged()
            })

        aMainBinding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (appBarLayout.totalScrollRange == 0 || verticalOffset == 0) {
                    aMainBinding.txlayoutInAppbar.visibility = View.VISIBLE
                    aMainBinding.txlayoutInToolbar.visibility = View.INVISIBLE
                } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
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