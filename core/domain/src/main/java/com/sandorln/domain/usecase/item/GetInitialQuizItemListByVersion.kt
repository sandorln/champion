package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInitialQuizItemListByVersion @Inject constructor(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(version: String) = runCatching {
        itemRepository
            .getItemListByVersion(version)
            .filter {
                it.inStore &&
                        it.tags.none { tag -> tag == ItemTagType.Consumable } &&
                        (it.mapType == MapType.SUMMONER_RIFT || it.mapType == MapType.ALL)
            }
    }
}