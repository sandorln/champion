package com.sandorln.champion.usecase

import com.sandorln.model.ItemData
import com.sandorln.model.result.ResultData
import com.sandorln.champion.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class FindItemByIdUseCase(
    private val getVersionUseCase: GetVersionUseCase,
    private val itemRepository: ItemRepository,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase
) {
    operator fun invoke(itemId: String): Flow<com.sandorln.model.result.ResultData<com.sandorln.model.ItemData>> =
        getVersionUseCase()
            .flatMapLatest { totalVersion ->
                flow {
                    val languageCode = getLanguageCodeUseCase()
                    emit(com.sandorln.model.result.ResultData.Loading)
                    val item = itemRepository.findItemById(
                        itemVersion = totalVersion,
                        itemId = itemId,
                        languageCode = languageCode
                    )
                    emit(com.sandorln.model.result.ResultData.Success(item))
                }.catch {
                    emit(com.sandorln.model.result.ResultData.Failed(Exception(it)))
                }
            }
}