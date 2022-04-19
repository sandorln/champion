package com.sandorln.champion.use_case

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.*

class GetChampionList(
    private val versionRepository: VersionRepository,
    private val championRepository: ChampionRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        versionRepository
            .getLolVersionCategory()
            .mapLatest { it.champion }
            .flatMapLatest { championVersion ->
                if (championVersion.isEmpty())
                    flow { emit(ResultData.Failed(Exception("챔피언 버전 정보가 없습니다"))) }
                else
                    championRepository.getChampionList(championVersion, search)
            }
}