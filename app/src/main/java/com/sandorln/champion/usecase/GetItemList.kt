package com.sandorln.champion.usecase

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetItemList(
    private val getVersion: GetVersion,
    private val itemRepository: ItemRepository
) {
    operator fun invoke(search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>> =
        getVersion()
            .flatMapLatest { totalVersion ->
                flow {
                    emit(ResultData.Loading)
                    val itemList = itemRepository.getItemList(totalVersion, search, inStore)
                    emit(ResultData.Success(itemList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}