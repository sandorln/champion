package com.sandorln.data.repository.sprite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.VersionDao
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.version.Version
import com.sandorln.model.exception.SpriteException
import com.sandorln.network.service.SpriteService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject

class DefaultSpriteRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val spriteService: SpriteService,
    private val versionDao: VersionDao
) : SpriteRepository {
    private val _spriteMutex = Mutex()
    private val _fileDirPath = context.filesDir.path

    override suspend fun refreshDownloadSpriteBitmap(version: Version, spriteType: SpriteType, fileNameList: List<String>): Result<Any> =
        _spriteMutex.withLock {
            coroutineScope {
                runCatching {
                    val versionDirPath = "$_fileDirPath${File.separator}${version.name}"
                    val versionDir = File(versionDirPath)
                    if (!versionDir.exists()) {
                        versionDir.mkdirs()
                    }

                    val downloadAndSaveDeferred = fileNameList.map { fileName ->
                        async {
                            val bitmapFile = File("${versionDirPath}${File.separator}${fileName}")
                            if (!bitmapFile.exists()) {
                                val spriteInputStream = spriteService.getSpriteFile(version.name, fileName)
                                bitmapFile.outputStream().write(spriteInputStream.readBytes())
                                spriteInputStream.close()
                            }
                        }
                    }

                    downloadAndSaveDeferred.awaitAll()

                    val tempVersion = when (spriteType) {
                        SpriteType.Champion -> version.copy(isDownLoadChampionIconSprite = true)
                        SpriteType.Item -> version.copy(isDownLoadItemIconSprite = true)
                        SpriteType.Spell -> version.copy(isDownLoadSpellIconSprite = true)
                    }

                    versionDao.insertVersion(tempVersion.asEntity())
                }
            }
        }

    override suspend fun getSpriteBitmap(version: String): Map<String, Bitmap> =
        _spriteMutex.withLock {
            runCatching {
                val spriteImageMap = mutableMapOf<String, Bitmap>()

                val versionDirPath = "$_fileDirPath${File.separator}$version"
                val versionDir = File(versionDirPath)
                if (!versionDir.exists()) throw SpriteException.NotFoundSpriteFileInFolder

                versionDir.listFiles()?.forEach { spriteFile ->
                    val spriteBitmap = BitmapFactory.decodeStream(spriteFile.inputStream())
                    spriteImageMap[spriteFile.name] = spriteBitmap
                } ?: throw SpriteException.NotFoundSpriteFileInFolder

                spriteImageMap
            }.getOrNull() ?: emptyMap()
        }
}