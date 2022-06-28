package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.sandorln.champion.R
import com.sandorln.champion.databinding.CustomChampionTipsBinding
import com.sandorln.champion.util.toPixel

class ChampionTips : FrameLayout {
    val binding = CustomChampionTipsBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}

@BindingAdapter("tips")
fun ChampionTips.setTips(tips: List<String>) {
    binding.layoutTips.removeAllViews()
    if (tips.isEmpty())
        binding.root.isVisible = false
    else
        tips.forEach { tip ->
            val tipView = TextView(ContextThemeWrapper(context, R.style.Base_Widget_AppCompat_TextView_TipStyle), null, 0).apply {
                text = tip
                layoutParams = LinearLayoutCompat.LayoutParams(binding.layoutTips.layoutParams).apply { setMargins(0, 10.toPixel(context), 0, 0) }
            }
            binding.layoutTips.addView(tipView)
        }
}

@BindingAdapter("tipsTitle")
fun ChampionTips.setTipsTitle(title: String) {
    binding.tvTitle.titleName = title
}