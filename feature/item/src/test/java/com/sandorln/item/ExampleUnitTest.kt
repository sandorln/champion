package com.sandorln.item

import com.sandorln.model.type.ItemTagType
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

    @Test
    fun 아이템확인() {
        val uiSelect = setOf(ItemTagType.Boots)
        val bootsItemTag = setOf(ItemTagType.Boots, ItemTagType.Armor)

        val filterPass = uiSelect.containsAll(bootsItemTag)
        val reversFilterPass = bootsItemTag.containsAll(uiSelect)

        println("filterPass = $filterPass , reversFilterPass = $reversFilterPass")
    }

    @Test
    fun 버전확인() {
        val versionList = listOf(
            "3.1.00",
            "3.10.00",
            "2.30.66",
            "13.24.1",
            "13.23.1",
            "13.4.1",
            "0.154.3",
            "14.2.1",
            "3.6.14"
        )

        val versionComparator = Comparator<String> { version1, version2 ->
            val (major1, minor1, patch1) = version1.split('.').map { it.toInt() }
            val (major2, minor2, patch2) = version2.split('.').map { it.toInt() }

            when {
                major1 != major2 -> major2 - major1
                minor1 != minor2 -> minor2 - minor1
                else -> patch2 - patch1
            }
        }

        val sortedVersionList = versionList.sortedWith(versionComparator)

        println("Sorted Version List: $sortedVersionList")
    }
}