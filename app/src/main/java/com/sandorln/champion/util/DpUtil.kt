package com.sandorln.champion.util

import android.content.Context
import android.util.TypedValue

fun Int.toPixel(context: Context): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()