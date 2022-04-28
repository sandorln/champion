package com.sandorln.champion.view.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sandorln.champion.R
import com.sandorln.champion.databinding.FragmentItemListBinding
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.view.adapter.ItemThumbnailAdapter
import com.sandorln.champion.view.base.BaseFragment
import com.sandorln.champion.view.dialog.ItemDataInfoDialog
import com.sandorln.champion.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ItemListFragment : BaseFragment<FragmentItemListBinding>(R.layout.fragment_item_list) {
    /* ViewModels */
    private val itemViewModel: ItemViewModel by viewModels()

    /* Adapters */
    private lateinit var itemThumbnailAdapter: ItemThumbnailAdapter

    override fun initObjectSetting() {
        itemThumbnailAdapter = ItemThumbnailAdapter { itemId ->
            ItemDataInfoDialog.newInstance(itemId).show(childFragmentManager, ItemDataInfoDialog::class.java.name)
        }
    }

    override fun initViewSetting() {
        binding.rvItemList.setHasFixedSize(true)
        binding.rvItemList.adapter = itemThumbnailAdapter
        binding.error.retry = { itemViewModel.refreshItemList() }
        binding.refreshItem.setOnRefreshListener { itemViewModel.refreshItemList() }
    }

    override fun initObserverSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    itemViewModel
                        .itemList
                        .collectLatest { result ->
                            binding.refreshItem.isRefreshing = false
                            binding.pbContent.isVisible = result is ResultData.Loading
                            binding.error.isVisible = result is ResultData.Failed

                            val itemList = when (result) {
                                is ResultData.Success -> result.data ?: mutableListOf()
                                is ResultData.Failed -> {
                                    binding.error.errorMsg = result.exception.message ?: "오류 발생"
                                    result.data ?: mutableListOf()
                                }
                                else -> mutableListOf()
                            }

                            itemThumbnailAdapter.submitList(itemList)
                        }
                }

                launch {
                    binding
                        .searchBar
                        .inputTextFlow
                        .collectLatest { search ->
                            itemViewModel.changeSearchItemName(search)
                        }
                }
            }
        }
    }
}