package com.sandorln.design.component.item

import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import org.xml.sax.XMLReader

@Composable
fun ItemDescriptionTextView(
    modifier: Modifier = Modifier,
    itemDescription: String = ""
) {
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                setTextColor(Colors.Gray05.hashCode())
                typeface = ResourcesCompat.getFont(context, R.font.warhaven_regular)
                text = Html.fromHtml(itemDescription, HtmlCompat.FROM_HTML_MODE_LEGACY, null, ItemHtmlTagHandler())
            }
        },
        update = {
            it.text = Html.fromHtml(itemDescription, HtmlCompat.FROM_HTML_MODE_LEGACY, null, ItemHtmlTagHandler())
        }
    )
}

enum class UniqueTag {
    PASSIVE,
    UNIQUE,
    ACTIVE
}

enum class StatsTag {
    STATS,
    ATTENTION,
    SPEED,
    PHYSICALDAMAGE
}

private val uniqueTagNameList = UniqueTag.entries.map { it.name.lowercase() }
private val statsTagNameList = StatsTag.entries.map { it.name.lowercase() }

class ItemHtmlTagHandler : Html.TagHandler {
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        val lowerTag = tag.lowercase()
        when {
            uniqueTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Gold02.hashCode()))
            }

            statsTagNameList.contains(lowerTag) -> {
                processTag(opening, output, ForegroundColorSpan(Colors.Gray03.hashCode()))
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