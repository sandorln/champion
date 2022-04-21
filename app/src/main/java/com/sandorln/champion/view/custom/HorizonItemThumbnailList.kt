package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.sandorln.champion.R
import com.sandorln.champion.databinding.CustomHorizonItemThumbnailBinding

class HorizonItemThumbnailList : LinearLayoutCompat {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    lateinit var binding: CustomHorizonItemThumbnailBinding
    fun setItemIdList(itemIdList: List<String>, itemVersion: String, onClickItemListener: (itemId: String) -> Unit) {
        if (itemIdList.isNotEmpty()) {
            binding.root.isVisible = true
            binding.layoutItems.removeAllViews()
            itemIdList.forEach { itemId ->
                val versionImage = VersionImage(context)
                versionImage.setItemThumbnail(itemVersion, itemId)
                versionImage.setOnClickListener { onClickItemListener(itemId) }
                binding.layoutItems.addView(versionImage)
                versionImage.updateLayoutParams<LinearLayout.LayoutParams> {
                    width = 100
                    height = 100
                }
            }
        } else {
            binding.root.isVisible = false
        }
    }

    private fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        binding = CustomHorizonItemThumbnailBinding.inflate(LayoutInflater.from(context), null, false)
        attrs?.let {
            val attrsValue = context.obtainStyledAttributes(it, R.styleable.HorizonItemThumbnailList, defStyleAttr, 0)
            binding.tvTitle.titleName = attrsValue.getString(R.styleable.HorizonItemThumbnailList_itemListTitleName)?.uppercase() ?: ""
            attrsValue.recycle()
        }
        addView(binding.root)
    }
}