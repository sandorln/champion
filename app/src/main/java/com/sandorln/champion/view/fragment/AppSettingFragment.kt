package com.sandorln.champion.view.fragment

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentAppSettingBinding
import com.sandorln.model.type.AppSettingType
import com.sandorln.champion.util.showStringListPopup
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.AppSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class AppSettingFragment : BaseFragment<FragmentAppSettingBinding>(R.layout.fragment_app_setting) {
    /* ViewModels */
    private val appSettingViewModel: AppSettingViewModel by viewModels()
    lateinit var onTouchListener: View.OnTouchListener

    override fun initObjectSetting() {
        onTouchListener = View.OnTouchListener { view, motionEvent ->
            if (motionEvent != null && motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                when (view.id) {
                    binding.checkQuestionNewestLolVersion.id -> com.sandorln.model.type.AppSettingType.QUESTION_NEWEST_LOL_VERSION
                    binding.checkVideoWifiModeOnlyPlay.id -> com.sandorln.model.type.AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY
                    else -> null
                }?.let { appSettingType ->
                    appSettingViewModel.changeAppSetting(appSettingType)
                }

                true
            } else
                false
        }
    }

    override fun initViewSetting() {
        binding.cardVersion.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                val versionList: List<String> = appSettingViewModel.versionList.firstOrNull() ?: mutableListOf()
                PopupWindow(requireContext()).showStringListPopup(binding.tvVersion, versionList) { selectVersion ->
                    appSettingViewModel.changeLolVersion(selectVersion)
                }
            }
        }
        binding.checkQuestionNewestLolVersion.setOnTouchListener(onTouchListener)
        binding.checkVideoWifiModeOnlyPlay.setOnTouchListener(onTouchListener)
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

                launch {
                    appSettingViewModel
                        .questionNewestLolVersion
                        .collectLatest {
                            binding.checkQuestionNewestLolVersion.isChecked = it
                        }
                }
                launch {
                    appSettingViewModel
                        .videoWifiModeAutoPlay
                        .collectLatest {
                            binding.checkVideoWifiModeOnlyPlay.isChecked = it
                        }
                }
            }
        }
    }
}