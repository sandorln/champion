package com.sandorln.champion.view.dialog

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
import com.sandorln.champion.databinding.DialogItemDataInfoBinding
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.util.removeBrFromHtml
import com.sandorln.champion.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ItemDataInfoDialog : DialogFragment() {
    companion object {
        fun newInstance(itemId: String): ItemDataInfoDialog {
            val args = Bundle()
            args.putString("itemId", itemId)
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
                                        tvName.text = item.name
                                        tvGold.text = item.gold.toString()

//                                        tvDescription.text = item.description.removeBrFromHtml()
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
}