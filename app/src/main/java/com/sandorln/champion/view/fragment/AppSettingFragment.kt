package com.sandorln.champion.view.fragment

import android.view.View
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentAppSettingBinding
import com.sandorln.champion.util.showStringListPopup
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.AppSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AppSettingFragment : BaseFragment<FragmentAppSettingBinding>(R.layout.fragment_app_setting) {
    /* ViewModels */
    private val appSettingViewModel: AppSettingViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
        val changeVersionListener = View.OnClickListener {
            lifecycleScope.launchWhenResumed {
                val versionList: List<String> = appSettingViewModel.versionList.firstOrNull() ?: mutableListOf()
                PopupWindow(requireContext()).showStringListPopup(binding.tvVersion, versionList) { selectVersion ->
                    appSettingViewModel.changeLolVersion(selectVersion)
                }
            }
        }

        binding.imgDown.setOnClickListener(changeVersionListener)
        binding.tvVersion.setOnClickListener(changeVersionListener)
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    appSettingViewModel
                        .version
                        .collectLatest { version ->
                            binding.tvVersion.text = version
                        }
                }
            }
        }
    }
}