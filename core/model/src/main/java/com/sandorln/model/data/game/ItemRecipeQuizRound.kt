package com.sandorln.model.data.game

import com.sandorln.model.data.item.ItemData

data class ItemRecipeQuizRound(
    val targetItem: ItemData = ItemData(),
    val requiredLeafItems: Map<ItemData, Int> = emptyMap(),
    val candidateLeafItems: List<ItemData> = emptyList()
) {
    val totalRequiredCount: Int
        get() = requiredLeafItems.values.sum()
}
