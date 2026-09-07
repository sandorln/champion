package com.sandorln.domain.usecase

import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.rune.RuneRepository
import com.sandorln.data.repository.spell.SummonerSpellRepository
import com.sandorln.data.repository.version.VersionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshAppStartData @Inject constructor(
    private val versionRepository: VersionRepository,
    private val spellRepository: SummonerSpellRepository,
    private val championRepository: ChampionRepository,
    private val itemRepository: ItemRepository,
    private val runeRepository: RuneRepository,
) {
    /**
     * 앱을 새롭게 시작할 때 버전 및 초기화가 이뤄지지 않은 버전들 새롭게 초기화 처리
     * [info] 룬 시스템은 Version [8.1.1] 이상부터 존재
     */
    suspend operator fun invoke() {
        runCatching {
            coroutineScope {
                /* Server 데이터로 갱신 */
                versionRepository.refreshVersionList()

                /* 초기 값이 완료 되지 않은 버전 다시 갱신 (최신 미완료 버전 1개만 우선 갱신하여 OOM 방지) */
                versionRepository.getNotInitCompleteVersionList().take(1).map { version ->
                    async {
                        val versionName = version.name

                        val championResult = if (version.isCompleteChampions)
                            true
                        else
                            championRepository.refreshChampionList(versionName).isSuccess

                        val itemResult = if (version.isCompleteItems)
                            true
                        else
                            itemRepository.refreshItemList(versionName).isSuccess

                        val spellResult = if (version.isCompleteSummonerSpell)
                            true
                        else
                            spellRepository.refreshSummonerSpellList(versionName).isSuccess

                        val (major, minor, patch) = version.majorMinorPatch
                        val isGreaterThanOrEqualTo811 =
                            when {
                                major > 8 -> true
                                major == 8 && minor > 1 -> true
                                major == 8 && minor == 1 && patch >= 1 -> true
                                else -> false
                            }

                        val runeResult = if (!isGreaterThanOrEqualTo811 || version.isCompleteRune)
                            true
                        else
                            runeRepository.refreshRuneStyleList(versionName).isSuccess

                        versionRepository.updateVersionData(
                            version.copy(
                                isCompleteChampions = championResult,
                                isCompleteItems = itemResult,
                                isCompleteSummonerSpell = spellResult,
                                isCompleteRune = runeResult,
                            )
                        )
                    }
                }.awaitAll()

                /* 이전 버전과 비교 하여 비교 값 저장 */
                val allVersionList = versionRepository.allVersionList.firstOrNull() ?: return@coroutineScope
                allVersionList.mapIndexed { index, version ->
                    async {
                        if (!version.isCompleteChampions || !version.isCompleteItems)
                            return@async

                        val preVersion = allVersionList.getOrNull(index + 1)
                        val preVersionName = preVersion?.name ?: ""
                        if (preVersion?.isCompleteChampions == false)
                            return@async

                        var newItemIdList: List<String>? = version.newItemIdList
                        var newChampionIdList: List<String>? = version.newChampionIdList

                        if (newItemIdList != null && newChampionIdList != null)
                            return@async

                        if (newItemIdList == null) {
                            newItemIdList = itemRepository.getNewItemIdList(
                                versionName = version.name,
                                preVersionName = preVersionName
                            )
                        }

                        if (newChampionIdList == null) {
                            newChampionIdList = championRepository.getNewChampionIdList(
                                versionName = version.name,
                                preVersionName = preVersionName
                            )
                        }

                        versionRepository.updateVersionData(
                            version.copy(
                                newChampionIdList = newChampionIdList,
                                newItemIdList = newItemIdList
                            )
                        )
                    }
                }.awaitAll()
            }
        }
    }
}