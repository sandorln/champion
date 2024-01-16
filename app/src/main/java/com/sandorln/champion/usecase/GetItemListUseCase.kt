package com.sandorln.champion.usecase

import com.sandorln.model.ItemData
import com.sandorln.model.result.ResultData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemListUseCase @Inject constructor() {
    operator fun invoke(search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>> =
        flow {
            emit(ResultData.Loading)
            /* 너무 빠르게 진행 시 해당 값이 무시됨 */
            delay(250)
            emit(ResultData.Success(emptyList<ItemData>()))
        }.catch {
            emit(ResultData.Failed(Exception(it)))
        }

}