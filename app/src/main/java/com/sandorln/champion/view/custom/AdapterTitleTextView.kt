package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sandorln.champion.R
import com.sandorln.champion.databinding.CustomAdapterTitleBinding

class AdapterTitleTextView : FrameLayout {
    val binding: CustomAdapterTitleBinding = CustomAdapterTitleBinding.inflate(LayoutInflater.from(context), this, true)
    var titleName: String = ""
        set(value) {
            field = value
            binding.tvTitle.text = value.uppercase()
        }

    constructor(context: Context) : super(context) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews(context, attrs, defStyleAttr)
    }

    private fun initViews(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        attrs?.let {
            val attrsValue = context.obtainStyledAttributes(it, R.styleable.AdapterTitleTextView, defStyleAttr, 0)
            titleName = attrsValue.getString(R.styleable.AdapterTitleTextView_titleName) ?: ""
            attrsValue.recycle()
        }
    }
}