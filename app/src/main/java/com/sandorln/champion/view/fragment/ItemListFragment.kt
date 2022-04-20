package com.sandorln.champion.view.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentItemListBinding
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ItemListFragment : BaseFragment<FragmentItemListBinding>(R.layout.fragment_item_list) {
    /* ViewModels */
    private val itemViewModel: ItemViewModel by viewModels()

    override fun initObjectSetting() {
    }

    override fun initViewSetting() {
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    itemViewModel
                        .itemList
                        .collectLatest {
                            Log.d("LOGD", "item List = $it")
                        }
                }
            }
        }
    }
}