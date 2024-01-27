package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.domain.usecase.version.GetCurrentVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewItemListByCurrentVersion @Inject constructor(
    private val getCurrentVersion: GetCurrentVersion,
    private val getAllVersionList: GetAllVersionList,
    private val itemRepository: ItemRepository
) {

    operator fun invoke() = combineTransform(
        getCurrentVersion.invoke().distinctUntilChangedBy { it.name },
        getAllVersionList.invoke().distinctUntilChangedBy { it.size }
    ) { currentVersion, allVersionList ->
        emit(emptyList())

        val currentVersionName = currentVersion.name
        val currentIndex = allVersionList.indexOfFirst { it.name == currentVersionName }
        val preVersionName = runCatching { allVersionList[currentIndex + 1] }.getOrNull()?.name ?: ""

        val newItemList = itemRepository.getNewItemListByCurrentVersion(
            currentVersionName = currentVersionName,
            preVersionName = preVersionName
        )

        emit(newItemList)
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
}