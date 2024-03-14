package com.sandorln.domain.usecase.sprite

import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.model.data.image.SpriteType
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshDownloadSpriteBitmap @Inject constructor(
    private val versionRepository: VersionRepository,
    private val spriteRepository: SpriteRepository
) {
    suspend operator fun invoke(spriteType: SpriteType, fileNameList: List<String>): Result<Any> {
        val currentVersion = versionRepository.currentVersion.firstOrNull() ?: return Result.failure(Exception(""))
        return spriteRepository.refreshDownloadSpriteBitmap(
            version = currentVersion,
            spriteType = spriteType,
            fileNameList = fileNameList
        )
    }
}