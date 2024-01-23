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
}