package com.sandorln.champion.usecase

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetItemListUseCase(
    private val getVersionUseCase: GetVersionUseCase,
    private val itemRepository: ItemRepository
) {
    operator fun invoke(search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>> =
        getVersionUseCase()
            .flatMapLatest { totalVersion ->
                flow {
                    emit(ResultData.Loading)
                    /* 너무 빠르게 진행 시 해당 값이 무시됨 */
                    delay(250)
                    val itemList = itemRepository.getItemList(totalVersion, search, inStore)
                    emit(ResultData.Success(itemList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}