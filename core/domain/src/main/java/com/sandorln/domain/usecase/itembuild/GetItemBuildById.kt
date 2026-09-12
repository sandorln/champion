package com.sandorln.domain.usecase.itembuild

import com.sandorln.data.repository.item.ItemBuildRepository
import com.sandorln.model.data.item.ItemBuild
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemBuildById @Inject constructor(
    private val itemBuildRepository: ItemBuildRepository
) {
    operator fun invoke(id: Long): Flow<ItemBuild?> =
        itemBuildRepository.getItemBuildById(id)
}
