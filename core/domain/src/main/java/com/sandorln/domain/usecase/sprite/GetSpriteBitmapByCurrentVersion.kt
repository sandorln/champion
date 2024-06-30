package com.sandorln.domain.usecase.sprite

import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.model.data.image.SpriteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSpriteBitmapByCurrentVersion @Inject constructor(
    private val getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    private val spriteRepository: SpriteRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(spriteType: SpriteType) = getCurrentVersionDistinctBySpriteType
        .invoke(spriteType)
        .transformLatest { version ->
            emit(emptyMap())
            emit(spriteRepository.getSpriteBitmap(version = version.name))
        }.flowOn(Dispatchers.IO)
}