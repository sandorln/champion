package com.sandorln.domain.usecase.itembuild

import com.sandorln.data.repository.item.ItemBuildRepository
import com.sandorln.model.data.item.ItemBuild
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemBuildListByVersion @Inject constructor(
    private val itemBuildRepository: ItemBuildRepository
) {
    operator fun invoke(version: String): Flow<List<ItemBuild>> =
        itemBuildRepository.getItemBuildListByVersion(version)
}
