package com.sandorln.domain.usecase.sprite

import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.model.data.image.SpriteType
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentVersionDistinctBySpriteType @Inject constructor(
    private val versionRepository: VersionRepository
) {
    /**
     * 해당 [SpriteType]의 파일 다운로드 확인 여부가 변경 되는지 확인
     */
    operator fun invoke(spriteType: SpriteType) = versionRepository
        .currentVersion
        .distinctUntilChanged { old, new ->
            val nameDiff = old.name == new.name
            val typeDiff = when (spriteType) {
                SpriteType.Champion -> old.isDownLoadChampionIconSprite && new.isDownLoadChampionIconSprite
                SpriteType.Item -> old.isDownLoadItemIconSprite && new.isDownLoadItemIconSprite
                SpriteType.Spell -> old.isDownLoadSpellIconSprite && new.isDownLoadSpellIconSprite
            }
            nameDiff && typeDiff
        }
}