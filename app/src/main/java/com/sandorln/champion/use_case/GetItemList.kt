package com.sandorln.champion.use_case

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

class GetItemList(
    private val getVersionCategory: GetVersionCategory,
    private val itemRepository: ItemRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ItemData>>> =
        getVersionCategory()
            .mapLatest { it.item }
            .flatMapLatest { itemVersion ->
                if (itemVersion.isEmpty())
                    flow { emit(ResultData.Failed(Exception("버전 정보를 알 수 없습니다"))) }
                else
                    itemRepository.getItemList(itemVersion, search, true)
            }
}