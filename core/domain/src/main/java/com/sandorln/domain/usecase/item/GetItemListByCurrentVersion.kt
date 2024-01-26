package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemListByCurrentVersion @Inject constructor(
    private val itemRepository: ItemRepository
) {
    operator fun invoke() = itemRepository.currentItemList
}