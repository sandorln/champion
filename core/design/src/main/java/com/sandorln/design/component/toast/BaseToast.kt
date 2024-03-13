package com.sandorln.design.component.toast

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.view.isVisible
import com.sandorln.design.R
import com.sandorln.design.databinding.CustomToastBinding
import com.sandorln.design.theme.Colors

enum class BaseToastType {
    TEXT,
    OKAY,
    WARNING
}

class BaseToast(
    context: Context,
    baseToastType: BaseToastType,
    messageText: String
) : Toast(context) {
    init {
        val iconId: Int?
        val iconColor: Color?

        when (baseToastType) {
            BaseToastType.TEXT -> {
                iconId = null
                iconColor = null
            }

            BaseToastType.OKAY -> {
                iconId = R.drawable.ic_check
                iconColor = Colors.Green00
            }

            BaseToastType.WARNING -> {
                iconId = R.drawable.ic_warning
                iconColor = Colors.Orange00
            }
        }

        val binding = CustomToastBinding.inflate(LayoutInflater.from(context), null, false)
        if (iconId != null) {
            binding.imgIcon.isVisible = true
            binding.imgIcon.setImageResource(iconId)
        }
        if (iconColor != null) {
            binding.imgIcon.imageTintList = ColorStateList.valueOf(iconColor.hashCode())
        }

        binding.tvContent.text = messageText

        setGravity(
            Gravity.BOTTOM or Gravity.FILL_HORIZONTAL,
            0,
            0
        )
        this.duration = LENGTH_SHORT
        this.view = binding.root
    }
}