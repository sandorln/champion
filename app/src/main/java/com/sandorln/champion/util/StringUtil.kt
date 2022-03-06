package com.sandorln.champion.util

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String.fromHtml(): Spanned {
    var tempString = this.replace("<br>", "")
    tempString = tempString.replace("</br>", "")

    return HtmlCompat.fromHtml(tempString, HtmlCompat.FROM_HTML_MODE_LEGACY)
}