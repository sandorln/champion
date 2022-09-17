package com.sandorln.champion.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.WindowMetricsCalculator
import com.sandorln.champion.databinding.DialogItemDataInfoBinding
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ItemDataInfoDialog : DialogFragment() {
    companion object {
        fun newInstance(itemId: String): ItemDataInfoDialog {
            val args = Bundle()
            args.putString(BundleKeys.ITEM_ID, itemId)
            val fragment = ItemDataInfoDialog()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: DialogItemDataInfoBinding

    /* ViewModels */
    private val itemViewModel: ItemViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogItemDataInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    itemViewModel
                        .findItemData
                        .collectLatest { result ->
                            when (result) {
                                is ResultData.Success -> {
                                    val item = result.data ?: throw Exception("아이템을 찾을 수 없습니다")
                                    with(binding) {
                                        imgItemThumbnail.setItemThumbnail(item.version, item.id)
                                        tvName.text = item.name
                                        tvGold.text = "${item.gold.total}G(팔 때 ${item.gold.sell}G)"

                                        val onClickItemThumbnail: (itemId: String) -> Unit = { itemId -> itemViewModel.changeFindItemId(itemId) }
                                        horizonItemNextDepth.setItemIdList(item.into, item.version, onClickItemThumbnail)
                                        horizonItemPreDepth.setItemIdList(item.from, item.version, onClickItemThumbnail)

                                        tvDescription.text = HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                    }
                                }
                                else -> {}
                            }
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WindowMetricsCalculator.getOrCreate().let { wmc ->
            val currentWM = wmc.computeCurrentWindowMetrics(requireActivity())
            val width = currentWM.bounds.width()

            val dialogLayoutManager = dialog?.window?.attributes ?: throw Exception("정보를 불러올 수 없습니다")
            dialogLayoutManager.width = (width * 0.8).toInt()

            dialog?.window?.apply {
                attributes = dialogLayoutManager
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }
}