package com.sandorln.champion.usecase

import kotlinx.coroutines.flow.lastOrNull
import javax.inject.Inject

class ChangeNewestVersionUseCase @Inject constructor(
    private val getVersionList: GetVersionListUseCase,
    private val changeVersion: ChangeVersionUseCase
) {
    suspend operator fun invoke() {
        val newestVersion = getVersionList().lastOrNull()?.first() ?: throw Exception("새로운 버전을 가져올 수 없습니다")
        changeVersion(newestVersion)
    }
}