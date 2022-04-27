package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.databinding.CustomErrorViewBinding

class ErrorView : FrameLayout {
    lateinit var binding: CustomErrorViewBinding

    constructor(context: Context) : super(context) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews(context)
    }

    var retry: suspend () -> Unit = {}
    var errorMsg: String = ""
        set(value) {
            binding.tvError.text = value
            field = value
        }

    private fun initViews(context: Context) {
        binding = CustomErrorViewBinding.inflate(LayoutInflater.from(context), null, false)
        binding.tvSubmit.setOnClickListener { findViewTreeLifecycleOwner()?.lifecycleScope?.launchWhenResumed { retry() } }
        addView(binding.root)
    }
}