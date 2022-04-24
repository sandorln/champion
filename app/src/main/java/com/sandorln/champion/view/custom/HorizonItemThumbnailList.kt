package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.sandorln.champion.R
import com.sandorln.champion.databinding.CustomHorizonItemThumbnailBinding
import com.sandorln.champion.model.SummonerSpell

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
                /* 롤 낮은 버전에서는 널 값을 줄때가 있음 */
                if (itemId != null) {
                    versionImage.setItemThumbnail(itemVersion, itemId)
                    versionImage.setOnClickListener { onClickItemListener(itemId) }
                    binding.layoutItems.addView(versionImage)
                    versionImage.updateLayoutParams<LinearLayout.LayoutParams> {
                        width = 100
                        height = 100
                    }
                }
            }
        } else {
            binding.root.isVisible = false
        }
    }

    fun setSummonerSpellList(summonerSpellList: List<SummonerSpell>, onClickSummonerSpellListener: (summonerSpell: SummonerSpell) -> Unit) {
        if (summonerSpellList.isNotEmpty()) {
            binding.root.isVisible = true
            binding.layoutItems.removeAllViews()

            summonerSpellList.forEach { summonerSpell ->
                val versionImage = VersionImage(context)
                versionImage.setSummonerSpellThumbnail(summonerSpell.version, summonerSpell.id)
                versionImage.setOnClickListener { onClickSummonerSpellListener(summonerSpell) }
                binding.layoutItems.addView(versionImage)
                versionImage.updateLayoutParams<LinearLayout.LayoutParams> {
                    width = 150
                    height = 150
                    setMargins(20)
                }
            }
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