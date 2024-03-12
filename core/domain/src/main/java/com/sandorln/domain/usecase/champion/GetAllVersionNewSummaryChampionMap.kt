package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.model.data.champion.SummaryChampion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllVersionNewSummaryChampionMap @Inject constructor(
    versionRepository: VersionRepository,
    private val championRepository: ChampionRepository
) {
    private val _allVersionNewChampionList = versionRepository
        .allVersionList
        .map { versionList ->
            versionList.map { version ->
                version.name to version.newChampionIdList
            }
        }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = _allVersionNewChampionList.mapLatest { allVersionNewChampionList ->
        val allVersionNewChampionMap = mutableMapOf<String, List<SummaryChampion>>()

        allVersionNewChampionList.forEach {
            val (version, championIdList) = it
            if (championIdList.isNullOrEmpty()) return@forEach

            allVersionNewChampionMap[version] = championRepository
                .getSummaryChampionListByChampionIdList(version, championIdList)
        }

        allVersionNewChampionMap
    }
}