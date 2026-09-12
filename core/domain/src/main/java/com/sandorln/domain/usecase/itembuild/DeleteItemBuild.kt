package com.sandorln.domain.usecase.itembuild

import com.sandorln.data.repository.item.ItemBuildRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteItemBuild @Inject constructor(
    private val itemBuildRepository: ItemBuildRepository
) {
    suspend operator fun invoke(id: Long) =
        itemBuildRepository.deleteItemBuild(id)
}
