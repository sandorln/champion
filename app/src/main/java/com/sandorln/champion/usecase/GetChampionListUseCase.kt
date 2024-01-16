package com.sandorln.champion.usecase

import com.sandorln.model.ChampionData
import com.sandorln.model.result.ResultData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 현재 설정된 TotalVersion 에 맞는 챔피언 목록을 가져옴
 *
 * @param getChampionVersionUseCase
 * 현재 선택된 TotalVersion 에 맞는 ChampionVersion 을 가져옴
 * 만일, 해당 값이 존재하지 않을 시 TotalVersion 을 되돌려줌
 */
@Singleton
class GetChampionListUseCase @Inject constructor() {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        flow {
            emit(ResultData.Loading)
            /* 너무 빠르게 진행 시 해당 값이 무시됨 */
            delay(250)
            emit(ResultData.Success(mutableListOf<ChampionData>()))
        }.catch {
            emit(ResultData.Failed(Exception(it)))
        }
}