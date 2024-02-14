package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemDataByItemId @Inject constructor(
    private val itemRepository: ItemRepository
) {

}