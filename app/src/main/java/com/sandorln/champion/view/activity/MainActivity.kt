package com.sandorln.champion.view.activity

import androidx.core.view.isVisible
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
        findNavController(R.id.frg_container).addOnDestinationChangedListener { _, destination, _ ->
            val mainFragmentDestinations = intArrayOf(R.id.frg_app_setting, R.id.frg_champion_list, R.id.frg_item_list, R.id.frg_summoner_spell_frg)
            binding.btmNaviMain.isVisible = mainFragmentDestinations.contains(destination.id)
        }
    }

    override fun initObserverSetting() {
    }

}