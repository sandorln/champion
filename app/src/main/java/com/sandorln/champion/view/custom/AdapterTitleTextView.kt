package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sandorln.champion.databinding.CustomAdapterTitleBinding

class AdapterTitleTextView : FrameLayout {
    private var binding: CustomAdapterTitleBinding = CustomAdapterTitleBinding.inflate(LayoutInflater.from(context), null, false)
    var text: String = ""
        set(value) {
            field = value 
            binding.tvTitle.text = value
            requestLayout()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(binding.root)
    }
}