package com.sandorln.champion.view.activity

import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityMainBinding
import com.sandorln.champion.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel: MainViewModel by viewModels()

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    mainViewModel
                        .currentSummaryChampionList
                        .collectLatest {
                            Log.d("ChampionList", "List Size : ${it.size}")
                            Log.d("ChampionList", "List : $it")
                        }
                }
            }
        }
    }
}