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
 * @param getChampionVersionUseCase
 * 현재 선택된 TotalVersion 에 맞는 ChampionVersion 을 가져옴
 * 만일, 해당 값이 존재하지 않을 시 TotalVersion 을 되돌려줌
 */
class GetChampionListUseCase(
    private val getChampionVersionUseCase: GetChampionVersionUseCase,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase,
    private val championRepository: ChampionRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        getChampionVersionUseCase()
            .flatMapLatest { championVersion ->
                flow {
                    val languageCode = getLanguageCodeUseCase()

                    if (championVersion.isEmpty())
                        throw Exception("버전 정보가 없습니다.")

                    emit(ResultData.Loading)
                    /* 너무 빠르게 진행 시 해당 값이 무시됨 */
                    delay(250)
                    val championList = championRepository.getChampionList(championVersion, search, languageCode)
                    emit(ResultData.Success(championList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}