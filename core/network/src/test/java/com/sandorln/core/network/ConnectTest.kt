package com.sandorln.core.network

import com.sandorln.network.service.ChampionService
import com.sandorln.network.service.ItemService
import com.sandorln.network.service.SpriteService
import com.sandorln.network.service.SummonerSpellService
import com.sandorln.network.service.VersionService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class ConnectTest {
    private lateinit var _versionService: VersionService
    private lateinit var _championService: ChampionService
    private lateinit var _itemService: ItemService
    private lateinit var _summonerSpellService: SummonerSpellService
    private lateinit var _spriteService: SpriteService

    @Before
    fun before() {
        val ktorClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }

            /* 모든 정보를 가져올 때 필요한 설정 */
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        _versionService = VersionService(ktorClient)
        _championService = ChampionService(ktorClient)
        _itemService = ItemService(ktorClient)
        _summonerSpellService = SummonerSpellService(ktorClient)
        _spriteService = SpriteService(ktorClient)
    }

    @Test
    fun 모든_버전_정보_가져오기() {
        runBlocking {
            runCatching {
                _versionService.getVersionList()
            }.fold(
                onSuccess = {
                    println("버전 정보가져오기 SUCCESS : $it")
                    println("버전 개수 : ${it.size}")
                },
                onFailure = {
                    println("버전 정보가져오기 ERROR : $it")
                }
            )
        }
    }

    @Test
    fun 모든_챔피온_정보_가져오기() {
        runBlocking {
            runCatching {
                val latestVersion = _versionService.getVersionList().first()
                _championService.getAllChampionDataMap(latestVersion)
            }.fold(
                onSuccess = {
                    println("챔피언 정보가져오기 SUCCESS : $it")
                },
                onFailure = {
                    println("챔피언 정보가져오기 ERROR : $it")
                }
            )
        }
    }

    @Test
    fun 모든_아이템_정보_가져오기() {
        runBlocking {
            runCatching {
                val latestVersion = _versionService.getVersionList().first()
                _itemService.getAllItemMap(latestVersion)
            }.fold(
                onSuccess = {
                    println("아이템 정보가져오기 SUCCESS : $it")
                },
                onFailure = {
                    println("아이템 정보가져오기 ERROR : $it")
                }
            )
        }
    }

    @Test
    fun 모든_서머너스펠_정보_가져오기() {
        runBlocking {
            runCatching {
                val latestVersion = _versionService.getVersionList().first()
                _summonerSpellService.getAllSummonerSpellMap(latestVersion)
            }.fold(
                onSuccess = {
                    println("서머너스펠 정보가져오기 SUCCESS : $it")
                },
                onFailure = {
                    println("서머너스펠 정보가져오기 ERROR : $it")
                }
            )
        }
    }

    @Test
    fun 챔피온_아이콘_스프라이트_가져오기() {
        runBlocking {
            runCatching {
                val latestVersion = _versionService.getVersionList().first()
                val championList = _championService.getAllChampionDataMap(latestVersion).values
                val spriteFileNameList = championList.map { it.image.sprite }.distinct()

                val time = measureTime {

                    val deferredList = spriteFileNameList.map {
                        async {
                            _spriteService.getChampionSpriteFile(latestVersion, it)
                        }
                    }

                    deferredList.awaitAll()
                }

                println("챔피온 아이콘 스프라이트를 다 받아오는 것에 걸린 시간 : $time")
            }.onFailure {
                println("챔피온 아이콘 스프라이트 ERROR : $it")
            }
        }
    }

    @Test
    fun 전체_버전_챔피언_아이콘_스프라이트_가져오기() {
        runBlocking {
            runCatching {
                var totalFileSize = 0
                val time = measureTimeMillis {
                    val totalDeferredList = _versionService.getVersionList().map { version ->
                        async {
                            val championList = _championService.getAllChampionDataMap(version).values
                            val spriteFileNameList = championList.map { it.image.sprite }.distinct()
                            val spriteDeferredList = spriteFileNameList.map { fileName ->
                                async {
                                    val file = _spriteService.getChampionSpriteFile(version, fileName)
                                    val size = file.readAllBytes().size
                                    totalFileSize += size
                                    println("fileName: $fileName, Size : $size byte")
                                    file.close()
                                }
                            }
                            spriteDeferredList.awaitAll()
                        }
                    }
                    totalDeferredList.awaitAll()
                }
                println("모든 챔피온 아이콘 스프라이트를 받아오는 것에 걸린 시간 : ${time.toFloat() / 1000} 초")
                println("최종 파일 사이즈 : ${totalFileSize.toFloat() / 1000000}mb")
            }
        }
    }
}