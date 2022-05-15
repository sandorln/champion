package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.sandorln.champion.databinding.CustomErrorViewBinding

class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: CustomErrorViewBinding = CustomErrorViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.tvSubmit.setOnClickListener { findViewTreeLifecycleOwner()?.lifecycleScope?.launchWhenResumed { retry() } }
    }

    var retry: suspend () -> Unit = {}
    var errorMsg: String = ""
        set(value) {
            binding.tvError.text = value
            field = value
        }

}