package com.sandorln.data.repository.sprite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sandorln.database.dao.SpriteImageDao
import com.sandorln.database.model.SpriteImageEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.network.service.SpriteService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSpriteRepository @Inject constructor(
    private val spriteService: SpriteService,
    private val spriteImageDao: SpriteImageDao,
    versionDatasource: VersionDatasource,
) : SpriteRepository {
    override val currentVersionSpriteFileMap: Flow<Map<String, Bitmap?>> = versionDatasource
        .currentVersion
        .flatMapLatest { version ->
            spriteImageDao.getSpriteImageList(version).map { spriteImageEntity ->
                spriteImageEntity.associate { entity -> entity.fileName to entity.image }
            }
        }

    override suspend fun refreshSpriteBitmap(version: String, fileNameList: List<String>): Result<Any> =
        runCatching {
            coroutineScope {
                val spriteImageEntityList = fileNameList.map { fileName ->
                    async {
                        val spriteInputStream = spriteService.getSpriteFile(version, fileName)
                        val bitmap = BitmapFactory.decodeStream(spriteInputStream)
                        spriteInputStream.close()

                        SpriteImageEntity(
                            fileName = fileName,
                            version = version,
                            image = bitmap
                        )
                    }
                }.awaitAll()

                spriteImageDao.insertSpriteImageList(spriteImageEntityList)
            }
        }
}