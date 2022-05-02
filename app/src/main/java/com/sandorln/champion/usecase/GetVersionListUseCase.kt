package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetVersionListUseCase(private val versionRepository: VersionRepository) {
    operator fun invoke(): Flow<List<String>> = flow {
        val lolVersionList = versionRepository.getLolVersionList()
        if (lolVersionList.isEmpty())
            throw Exception("롤 버전 정보를 가져올 수 없습니다")

        emit(lolVersionList)
    }
}