package com.sandorln.champion.view.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sandorln.champion.R
import com.sandorln.champion.data.CharacterData
import com.sandorln.champion.databinding.AMainBinding

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

        mainViewModel.characterList.observe(this,
            Observer<List<CharacterData>> { champList ->
                champAdapter.championList = champList
                champAdapter.notifyDataSetChanged()
            })
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllChampion()
    }
}