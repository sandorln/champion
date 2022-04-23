package com.sandorln.champion.usecase

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

class GetItemList(
    private val getVersion: GetVersion,
    private val itemRepository: ItemRepository
) {
    operator fun invoke(search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>> =
        getVersion()
            .flatMapLatest { itemVersion ->
                if (itemVersion.isEmpty())
                    flow { emit(ResultData.Failed(Exception("버전 정보를 알 수 없습니다"))) }
                else
                    itemRepository.getItemList(itemVersion, search, inStore)
            }
}