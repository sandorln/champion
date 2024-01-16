package com.sandorln.champion.usecase

import com.sandorln.model.ItemData
import com.sandorln.model.result.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindItemByIdUseCase @Inject constructor() {
    operator fun invoke(itemId: String): Flow<ResultData<ItemData>> = flow {
        emit(ResultData.Loading)
        emit(ResultData.Success(ItemData()))
    }.catch {
        emit(ResultData.Failed(Exception()))
    }
}