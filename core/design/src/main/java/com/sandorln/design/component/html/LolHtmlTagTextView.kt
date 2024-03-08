package com.sandorln.design.component.html

import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.TextStyles
import org.xml.sax.XMLReader

@Composable
fun LolHtmlTagTextView(
    modifier: Modifier = Modifier,
    textSize: Float = TextStyles.Body04.fontSize.value,
    textColor: Color = Colors.Gray04,
    lolDescription: String = ""
) {
    var tempLolDescription = if (!lolDescription.startsWith("<mainText>")) {
        "<mainText>$lolDescription</mainText>"
    } else {
        lolDescription
    }

    tempLolDescription = tempLolDescription
        .replace("<keywordMajor>", "<font color='#FFFFFF'><u>")
        .replace("</keywordMajor>", "</u></font>")
        .replace(Regex("\\{\\{.*?\\}\\}"), "<u>(?)</u>")
        .replace("{", "")
        .replace("}", "")
        .replace(Regex("\\(%(.*?)%\\)"), "")

    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                this.setTextColor(textColor.hashCode())
                this.textSize = textSize
                this.typeface = ResourcesCompat.getFont(context, R.font.warhaven_regular)
                this.text = Html.fromHtml(tempLolDescription, HtmlCompat.FROM_HTML_MODE_LEGACY, null, ItemHtmlTagHandler())
            }
        },
        update = {
            it.text = Html.fromHtml(tempLolDescription, HtmlCompat.FROM_HTML_MODE_LEGACY, null, ItemHtmlTagHandler())
        }
    )
}

private enum class GoldColorTag {
    PASSIVE,
    UNIQUE,
    ACTIVE,
    SPELLNAME,
    SPELLACTIVE,
    SPELLPASSIVE
}

private enum class GrayColorTag {
    STATS,
    ATTENTION,
    SPEED,
    STATUS
}

private enum class RedTag {
    PHYSICALDAMAGE
}

private enum class BlueTag {
    MAGICDAMAGE
}

private enum class GreenTag {
    HEALING
}

private val goldColorTagNameList = GoldColorTag.entries.map { it.name.lowercase() }
private val grayColorTagNameList = GrayColorTag.entries.map { it.name.lowercase() }
private val redColorTagNameList = RedTag.entries.map { it.name.lowercase() }
private val blueColorTagNameList = BlueTag.entries.map { it.name.lowercase() }
private val greenColorTagNameList = GreenTag.entries.map { it.name.lowercase() }

private class ItemHtmlTagHandler : Html.TagHandler {
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        val lowerTag = tag.lowercase()
        when {
            goldColorTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Gold02.hashCode()))
            }

            grayColorTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Gray02.hashCode()))
            }

            redColorTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Orange00.hashCode()))
            }

            blueColorTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Blue03.hashCode()))
            }

            greenColorTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Green00.hashCode()))
            }
        }
    }

    private fun processTag(opening: Boolean, output: Editable, span: Any) {
        val len = output.length

        if (opening) {
            output.setSpan(span, len, len, Spannable.SPAN_MARK_MARK)
        } else {
            val obj = getLast(output, span::class.java)
            val start = output.getSpanStart(obj)
            output.removeSpan(obj)
            if (start != len) {
                output.setSpan(span, start, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun getLast(text: Editable, kind: Class<*>): Any? {
        val obj = text.getSpans(0, text.length, kind)

        return if (obj.isEmpty()) {
            null
        } else {
            for (i in obj.size downTo 1) {
                if (text.getSpanFlags(obj[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return obj[i - 1]
                }
            }
            null
        }
    }
}