package com.sandorln.champion.usecase

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * 현재 설정된 TotalVersion 에 맞는 챔피언 목록을 가져옴
 *
 * @param getChampionVersion    현재 선택된 TotalVersion 에 맞는 ChampionVersion 을 가져옴
 *                              만일, 해당 값이 존재하지 않을 시 TotalVersion 을 되돌려줌
 */
class GetChampionList(
    private val getChampionVersion: GetChampionVersion,
    private val championRepository: ChampionRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        getChampionVersion()
            .flatMapLatest { championVersion ->
                flow {
                    if (championVersion.isEmpty())
                        throw Exception("버전 정보가 없습니다.")

                    emit(ResultData.Loading)
                    delay(250)
                    val championList = championRepository.getChampionList(championVersion, search)
                    emit(ResultData.Success(championList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}