package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.item.SummaryItemImage
import com.sandorln.model.data.map.MapType
import com.sandorln.model.data.patchnote.PatchNoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GetItemRecipeQuizRoundListTest {

    private class FakeItemRepository(
        private val items: List<ItemData>
    ) : ItemRepository {
        override val currentItemList: Flow<List<ItemData>> = emptyFlow()
        override suspend fun refreshItemList(version: String): Result<Any> = Result.success(Unit)
        override suspend fun getItemListByVersion(version: String): List<ItemData> = items
        override suspend fun getNewItemIdList(versionName: String, preVersionName: String): List<String> = emptyList()
        override suspend fun getItemDataByIdAndVersion(id: String, versionName: String): ItemData =
            items.first { it.id == id }
        override suspend fun getItemCombination(id: String, version: String): ItemCombination =
            ItemCombination()
        override suspend fun getSummaryItemImage(id: String, versionName: String): SummaryItemImage? = null
        override suspend fun getItemPatchList(version: String): List<PatchNoteData> = emptyList()
    }

    @Test
    fun testLeafItemDecompositionAndQuantity() = runBlocking {
        // 101: 쓸큰지 (기초 leaf)
        val leaf1 = ItemData(id = "101", name = "쓸큰지", inStore = true, mapType = MapType.SUMMONER_RIFT, from = emptyList())
        // 102: 증폭의 고서 (기초 leaf)
        val leaf2 = ItemData(id = "102", name = "증폭고서", inStore = true, mapType = MapType.SUMMONER_RIFT, from = emptyList())
        // 103: 루비 수정 (기초 leaf)
        val leaf3 = ItemData(id = "103", name = "루비수정", inStore = true, mapType = MapType.SUMMONER_RIFT, from = emptyList())
        // 104: 롱소드 (기초 leaf - distractor)
        val leaf4 = ItemData(id = "104", name = "롱소드", inStore = true, mapType = MapType.SUMMONER_RIFT, from = emptyList())

        // 201: 중간 서사 아이템 (from: 102 증폭고서) - 리프 아님
        val intermediate = ItemData(id = "201", name = "악마의 마법서", inStore = true, mapType = MapType.SUMMONER_RIFT, from = listOf("102"))

        // 301: 라바돈 (from: 101, 101) -> leaf: 쓸큰지 x 2
        val rabadon = ItemData(id = "301", name = "라바돈", inStore = true, mapType = MapType.SUMMONER_RIFT, from = listOf("101", "101"))

        // 302: 존야 (from: 201 중간템, 103 루비수정) -> leaf: 증폭고서(102) x 1, 루비수정(103) x 1
        val zhonya = ItemData(id = "302", name = "존야", inStore = true, mapType = MapType.SUMMONER_RIFT, from = listOf("201", "103"))

        val fakeRepo = FakeItemRepository(listOf(leaf1, leaf2, leaf3, leaf4, intermediate, rabadon, zhonya))
        val useCase = GetItemRecipeQuizRoundList(fakeRepo)

        val result = useCase(version = "14.13.1", roundCount = 2, random = Random(42))
        assertTrue(result.isSuccess)

        val rounds = result.getOrThrow()
        assertEquals(2, rounds.size)

        val rabadonRound = rounds.find { it.targetItem.id == "301" }!!
        assertEquals(1, rabadonRound.requiredLeafItems.size)
        assertEquals(2, rabadonRound.requiredLeafItems[leaf1])
        assertEquals(2, rabadonRound.totalRequiredCount)
        assertTrue(rabadonRound.candidateLeafItems.contains(leaf1))

        val zhonyaRound = rounds.find { it.targetItem.id == "302" }!!
        assertEquals(2, zhonyaRound.requiredLeafItems.size)
        assertEquals(1, zhonyaRound.requiredLeafItems[leaf2])
        assertEquals(1, zhonyaRound.requiredLeafItems[leaf3])
        assertEquals(2, zhonyaRound.totalRequiredCount)
        // intermediate should NOT be in requiredLeafItems
        assertTrue(!zhonyaRound.requiredLeafItems.containsKey(intermediate))
    }
}
