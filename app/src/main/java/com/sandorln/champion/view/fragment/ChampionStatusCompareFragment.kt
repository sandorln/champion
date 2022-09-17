package com.sandorln.champion.view.fragment

import android.view.View
import android.widget.PopupWindow
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentChampionStatusCompareBinding
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.util.showStringListPopup
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ChampionStatusCompareViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionStatusCompareFragment : BaseFragment<FragmentChampionStatusCompareBinding>(R.layout.fragment_champion_status_compare) {
    /* ViewModels */
    private val championStatusCompareViewModel: ChampionStatusCompareViewModel by viewModels()

    private lateinit var originalVersionChange: View.OnClickListener
    private lateinit var otherVersionChange: View.OnClickListener

    override fun initObjectSetting() {
        originalVersionChange = View.OnClickListener {
            lifecycleScope.launchWhenResumed {
                val versionList: List<String> = championStatusCompareViewModel.versionList.firstOrNull() ?: mutableListOf()
                PopupWindow(requireContext()).showStringListPopup(binding.tvOriginalVersion, versionList) { selectVersion ->
                    championStatusCompareViewModel.changeFirstVersion(selectVersion)
                }
            }
        }
        otherVersionChange = View.OnClickListener {
            lifecycleScope.launchWhenResumed {
                val versionList: List<String> = championStatusCompareViewModel.versionList.firstOrNull() ?: mutableListOf()
                PopupWindow(requireContext()).showStringListPopup(binding.tvOtherVersion, versionList) { selectVersion ->
                    championStatusCompareViewModel.changeSecondVersion(selectVersion)
                }
            }
        }
    }

    override fun initViewSetting() {
        binding.tvOriginalVersion.setOnClickListener(originalVersionChange)
        binding.frgOriginal.setOnClickListener(originalVersionChange)

        binding.tvOtherVersion.setOnClickListener(otherVersionChange)
        binding.frgOther.setOnClickListener(otherVersionChange)
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    with(championStatusCompareViewModel) {
                        combine(firstChampionStatus, firstChampionVersion, secondChampionStatus, secondChampionVersion) { firstStatus, firstVer, secondStatus, secondVer ->
                            binding.tvOriginalVersion.text = HtmlCompat.fromHtml(getString(R.string.version_title, firstVer), HtmlCompat.FROM_HTML_MODE_LEGACY)
                            binding.tvOtherVersion.text = HtmlCompat.fromHtml(getString(R.string.version_title, secondVer), HtmlCompat.FROM_HTML_MODE_LEGACY)

                            childFragmentManager
                                .beginTransaction()
                                .replace(
                                    binding.frgOriginal.id, ChampionStatusFragment::class.java, bundleOf(
                                        BundleKeys.CHAMPION_VERSION to firstVer,
                                        BundleKeys.CHAMPION_ORIGIN_STATUS_KEY to firstStatus,
                                        BundleKeys.CHAMPION_OTHER_STATUS_KEY to secondStatus
                                    )
                                )
                                .replace(
                                    binding.frgOther.id, ChampionStatusFragment::class.java, bundleOf(
                                        BundleKeys.CHAMPION_VERSION to secondVer,
                                        BundleKeys.CHAMPION_ORIGIN_STATUS_KEY to secondStatus,
                                        BundleKeys.CHAMPION_OTHER_STATUS_KEY to firstStatus
                                    )
                                )
                                .commitNowAllowingStateLoss()
                        }.collect()
                    }
                }
            }
        }
    }
}