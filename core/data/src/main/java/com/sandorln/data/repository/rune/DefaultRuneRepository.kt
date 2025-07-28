package com.sandorln.data.repository.rune

import com.sandorln.data.util.asData
import com.sandorln.data.util.toEntity
import com.sandorln.database.dao.RuneDao
import com.sandorln.database.model.RuneStyleEntity
import com.sandorln.datastore.local.version.VersionDatasource
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.data.rune.RuneStyle
import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.service.RuneService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRuneRepository @Inject constructor(
    versionDataSource: VersionDatasource,
    private val runeService: RuneService,
    private val runeDao: RuneDao
) : RuneRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentRuneStyleList: Flow<List<RuneStyle>> = versionDataSource
        .currentVersion
        .flatMapLatest { version ->
            runeDao.getAllRuneStyleList(version).map { entityList ->
                entityList.map(RuneStyleEntity::asData)
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun refreshRuneStyleList(version: String): Result<Any> = runCatching {
        val response = runeService.getAllRuneDataList(version)
        val runeEntityList = response.map { it.toEntity(version) }
        runeDao.insertRuneStyleList(runeEntityList)
    }

    override suspend fun getRunePatchList(version: String): List<PatchNoteData> =
        runeService.getRunePathNoteList(version).map(NetworkPatchNoteData::asData)
}