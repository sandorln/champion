package com.sandorln.home

import android.text.Annotation
import android.text.SpannableStringBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private val dummyData =
        "<mainText><stats>공격 속도 <attention> 15%</attention></stats><br><li><passive>충전 상태:</passive> 이동하거나 공격하면 충전 상태로 공격할 수 있습니다.<li>" +
                "<passive>충격:</passive> 충전 상태로 공격 시 <magicDamage>80의 마법 피해</magicDamage>를 추가로 입힙니다.</mainText><br>"

    @Test
    fun 어노테이션_테스트() {
        val lolHtml = dummyData.toLolHtml()
        SpannableStringBuilder(lolHtml).getSpans(0, lolHtml.length, Annotation::class.java).forEach {
            println("Span : $it")
        }
    }

    fun String.toLolHtml(): String {
        val document: Document = Jsoup.parse(this)
        val elements: Elements = document.select("body *")
        val tagNames: MutableList<String> = ArrayList()
        val values: MutableList<String> = ArrayList()
        for (element in elements) {
            val tagName: String = element.tagName()
            tagNames.add(tagName)
            if ("img" == tagName) {
                values.add(element.attr("src"))
            } else {
                values.add(element.html())
            }

            println("tag : ${element.tagName()} / value : ${element.html()}")
        }

        val tempString = this
        tagNames.forEach {
            val tag = when (it.lowercase()) {
                "attention" -> "color='#588FF8'"
                "stats" -> "fontsize='14'"
                else -> ""
            }

            tempString.replace("<$it>", "<annotation $tag>")
            tempString.replace("</$it>", "</annotation>")
        }
        return tempString
    }
}