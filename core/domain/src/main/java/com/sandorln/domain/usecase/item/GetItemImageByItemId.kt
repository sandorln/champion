package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSummaryItemImage @Inject constructor(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(id : String, version : String) = itemRepository.getSummaryItemImage(id, version)
}