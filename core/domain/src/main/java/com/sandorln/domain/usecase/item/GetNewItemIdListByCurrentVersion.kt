package com.sandorln.domain.usecase.item

import com.sandorln.data.repository.version.VersionRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNewItemIdListByCurrentVersion @Inject constructor(
    private val versionRepository: VersionRepository
) {
    operator fun invoke() = versionRepository
        .currentVersion
        .map { version ->
            version.newItemIdList ?: listOf()
        }.distinctUntilChanged()
}