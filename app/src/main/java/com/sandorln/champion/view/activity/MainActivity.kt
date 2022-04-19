package com.sandorln.champion.view.activity

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityMainBinding
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun initObjectSetting() {
        binding.btmNaviMain.setupWithNavController(findNavController(R.id.frg_container))
    }

    override fun initViewSetting() {

    }

    override fun initObserverSetting() {
    }

}