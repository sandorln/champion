package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.model.data.item.ItemCombination
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemCombination @Inject constructor(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(id: String, version: String): Result<ItemCombination> = runCatching {
        itemRepository.getItemCombination(id, version)
    }
}