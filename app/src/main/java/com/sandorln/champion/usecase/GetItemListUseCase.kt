package com.sandorln.champion.usecase

import com.sandorln.model.ItemData
import com.sandorln.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetItemListUseCase(
    private val getVersionUseCase: GetVersionUseCase,
    private val itemRepository: ItemRepository,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase
) {
    operator fun invoke(search: String, inStore: Boolean): Flow<com.sandorln.model.result.ResultData<List<com.sandorln.model.ItemData>>> =
        getVersionUseCase()
            .flatMapLatest { totalVersion ->
                flow {
                    emit(com.sandorln.model.result.ResultData.Loading)
                    /* 너무 빠르게 진행 시 해당 값이 무시됨 */
                    delay(250)
                    val languageCode = getLanguageCodeUseCase()
                    val itemList = itemRepository.getItemList(
                        itemVersion = totalVersion,
                        search = search,
                        inStore = inStore,
                        languageCode = languageCode
                    )
                    emit(com.sandorln.model.result.ResultData.Success(itemList))
                }.catch {
                    emit(com.sandorln.model.result.ResultData.Failed(Exception(it)))
                }
            }
}