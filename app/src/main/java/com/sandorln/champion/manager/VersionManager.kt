package com.sandorln.champion.manager

import com.sandorln.champion.model.VersionLol
import com.sandorln.champion.network.VersionService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class VersionManager(private val versionService: VersionService) {
    companion object {
        var versionLol: VersionLol = VersionLol()
    }

    private val versionMutex = Mutex()
    suspend fun getVersion(): VersionLol =
        versionMutex.withLock {
            try {
                when {
                    versionLol.lvTotalVersion.isEmpty() ||
                            versionLol.lvCategory.cvChampion.isEmpty() ||
                            versionLol.lvCategory.cvLanguage.isEmpty() ||
                            versionLol.lvCategory.cvItem.isEmpty() -> {
                        versionLol = versionService.getVersion()
                        versionLol
                    }
                    else -> versionLol
                }
            } catch (e: Exception) {
                throw  e
            }
        }
}