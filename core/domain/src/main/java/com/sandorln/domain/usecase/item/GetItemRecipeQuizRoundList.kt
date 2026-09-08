package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.model.data.game.ItemRecipeQuizRound
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GetItemRecipeQuizRoundList @Inject constructor(
    private val itemRepository: ItemRepository
) {
    companion object {
        const val DEFAULT_ROUND_COUNT = 10
        const val CANDIDATE_POOL_SIZE = 16
    }

    suspend operator fun invoke(
        version: String,
        roundCount: Int = DEFAULT_ROUND_COUNT,
        random: Random = Random(System.currentTimeMillis())
    ): Result<List<ItemRecipeQuizRound>> = runCatching {
        val allValidItems = itemRepository
            .getItemListByVersion(version)
            .filter {
                it.inStore &&
                        it.tags.none { tag -> tag == ItemTagType.Consumable } &&
                        (it.mapType == MapType.SUMMONER_RIFT || it.mapType == MapType.ALL)
            }

        val itemMap = allValidItems.associateBy { it.id }

        // 재귀적으로 리프 아이템(더 이상 from이 없는 최소 기초 재료) ID 목록 추출
        fun getLeafItemIds(itemId: String): List<String> {
            val item = itemMap[itemId] ?: return emptyList()
            if (item.from.isEmpty()) {
                return listOf(itemId)
            }
            return item.from.flatMap { childId -> getLeafItemIds(childId) }
        }

        // 전체 기초 재료(Leaf) 풀
        val allLeafItems = allValidItems.filter { it.from.isEmpty() }

        // 조합식이 존재하며, 리프 재료가 2개 이상인 완성 아이템들
        val craftableItems = allValidItems.filter { it.from.isNotEmpty() }
            .mapNotNull { item ->
                val leafIds = getLeafItemIds(item.id)
                if (leafIds.size < 2) return@mapNotNull null
                val leafItemCounts = leafIds
                    .mapNotNull { itemMap[it] }
                    .groupingBy { it }
                    .eachCount()
                if (leafItemCounts.isEmpty()) return@mapNotNull null
                item to leafItemCounts
            }

        // 퀴즈 문제로 출제할 완성 아이템들을 무작위 셔플하여 roundCount개 선정
        val selectedCraftables = craftableItems.shuffled(random).take(roundCount)

        selectedCraftables.map { (targetItem, requiredLeafItems) ->
            val correctLeaves = requiredLeafItems.keys.toList()
            val distractorPool = allLeafItems.filter { it !in requiredLeafItems.keys }
            val neededDistractorCount = (CANDIDATE_POOL_SIZE - correctLeaves.size).coerceAtLeast(0)
            val selectedDistractors = distractorPool.shuffled(random).take(neededDistractorCount)

            val candidateList = (correctLeaves + selectedDistractors).shuffled(random)

            ItemRecipeQuizRound(
                targetItem = targetItem,
                requiredLeafItems = requiredLeafItems,
                candidateLeafItems = candidateList
            )
        }
    }
}
