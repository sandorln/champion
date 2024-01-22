package com.sandorln.data.repository.spell

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.SummonerSpellDao
import com.sandorln.database.model.SummonerSpellEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.model.data.spell.SummonerSpell
import com.sandorln.network.service.SummonerSpellService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSummonerSpellRepository @Inject constructor(
    versionDatasource: VersionDatasource,
    private val summonerSpellDao: SummonerSpellDao,
    private val summonerSpellService: SummonerSpellService
) : SummonerSpellRepository {
    override val currentItemList: Flow<List<SummonerSpell>> = versionDatasource
        .currentVersion
        .flatMapLatest { version ->
            summonerSpellDao.getAllSummonerSpell(version).map { entityList ->
                entityList.map(SummonerSpellEntity::asData)
            }
        }

    override suspend fun refreshSummonerSpellList(version: String): Result<Any> = runCatching {
        val response = summonerSpellService.getAllSummonerSpellMap(version)
        val spellEntityList = response.map { it.value.asEntity(version = version) }
        summonerSpellDao.insertSummonerSpellList(spellEntityList)
    }

    override suspend fun getSummonerSpellListByVersion(version: String): List<SummonerSpell> =
        summonerSpellDao.getAllSummonerSpell(version).firstOrNull()?.map(SummonerSpellEntity::asData) ?: emptyList()
}