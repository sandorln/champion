package com.sandorln.game.ui.recipequiz

import com.sandorln.model.data.item.ItemData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemRecipeQuizStateTest {

    @Test
    fun testUserCartAddRemoveClearLogic() {
        val leafA = ItemData(id = "101", name = "쓸큰지")
        val leafB = ItemData(id = "102", name = "증폭고서")

        var cart = mapOf<ItemData, Int>()

        // 1. Add leafA once
        cart = cart + (leafA to (cart[leafA] ?: 0) + 1)
        assertEquals(1, cart[leafA])

        // 2. Add leafA again (count: 2)
        cart = cart + (leafA to (cart[leafA] ?: 0) + 1)
        assertEquals(2, cart[leafA])

        // 3. Add leafB once
        cart = cart + (leafB to (cart[leafB] ?: 0) + 1)
        assertEquals(1, cart[leafB])
        assertEquals(3, cart.values.sum())

        // 4. Remove leafA once (count: 1)
        val countA = cart[leafA] ?: 0
        cart = if (countA <= 1) cart - leafA else cart + (leafA to countA - 1)
        assertEquals(1, cart[leafA])

        // 5. Remove leafB once (removed completely)
        val countB = cart[leafB] ?: 0
        cart = if (countB <= 1) cart - leafB else cart + (leafB to countB - 1)
        assertFalse(cart.containsKey(leafB))

        // 6. Clear cart
        cart = emptyMap()
        assertTrue(cart.isEmpty())
    }

    @Test
    fun testRecipeCraftAnswerMatching() {
        val leafA = ItemData(id = "101", name = "쓸큰지")
        val leafB = ItemData(id = "102", name = "증폭고서")

        val requiredMap = mapOf(leafA to 2, leafB to 1)

        // Case 1: Exactly matches
        val correctCart = mapOf(leafA to 2, leafB to 1)
        val isMatch1 = requiredMap.size == correctCart.size &&
                requiredMap.all { (item, count) -> correctCart[item] == count }
        assertTrue(isMatch1)

        // Case 2: Wrong quantity
        val wrongQuantityCart = mapOf(leafA to 1, leafB to 1)
        val isMatch2 = requiredMap.size == wrongQuantityCart.size &&
                requiredMap.all { (item, count) -> wrongQuantityCart[item] == count }
        assertFalse(isMatch2)

        // Case 3: Missing leafB
        val missingCart = mapOf(leafA to 2)
        val isMatch3 = requiredMap.size == missingCart.size &&
                requiredMap.all { (item, count) -> missingCart[item] == count }
        assertFalse(isMatch3)
    }

    @Test
    fun testPartialScoringLogic() {
        // 톱날단검: 롱소드 2개 필요
        val longSword = ItemData(id = "1036", name = "롱소드")
        val ruby = ItemData(id = "1028", name = "루비수정")
        val requiredMap = mapOf(longSword to 2)

        // Case 1: 롱소드 1개만 넣었을 때 -> 1개 일치 -> 10점
        val cart1 = mapOf(longSword to 1)
        val matched1 = requiredMap.entries.sumOf { (item, reqCount) ->
            minOf(cart1[item] ?: 0, reqCount)
        }
        assertEquals(1, matched1)
        val score1 = matched1 * 10L
        assertEquals(10L, score1)

        // Case 2: 롱소드 1개 + 오답 루비수정 1개 넣었을 때 -> 1개 일치 -> 10점
        val cart2 = mapOf(longSword to 1, ruby to 1)
        val matched2 = requiredMap.entries.sumOf { (item, reqCount) ->
            minOf(cart2[item] ?: 0, reqCount)
        }
        assertEquals(1, matched2)
        val score2 = matched2 * 10L
        assertEquals(10L, score2)

        // Case 3: 롱소드 3개(초과) 넣었을 때 -> 최대 요구치 2개까지만 일치 인정 -> 20점
        val cart3 = mapOf(longSword to 3)
        val matched3 = requiredMap.entries.sumOf { (item, reqCount) ->
            minOf(cart3[item] ?: 0, reqCount)
        }
        assertEquals(2, matched3)
        val score3 = matched3 * 10L
        assertEquals(20L, score3)
    }
}
