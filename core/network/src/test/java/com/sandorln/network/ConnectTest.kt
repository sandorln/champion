package com.sandorln.network

import com.sandorln.network.service.ItemService
import com.sandorln.network.service.SpriteService
import com.sandorln.network.service.SummonerSpellService
import com.sandorln.network.service.VersionService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

class ConnectTest {
    private lateinit var _versionService: VersionService
    private lateinit var _itemService: ItemService
    private lateinit var _summonerSpellService: SummonerSpellService
    private lateinit var _spriteService: SpriteService

    @Before
    fun before() {
        val ktorClient = HttpClient {
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
    fun 아이템_패치노트_가져오기() {
        runBlocking {
            runCatching {
                _itemService.getItemPathNoteList("14.15.1")
            }.onSuccess {
                println("이번 아이템 패치 노트 : $it")
            }.onFailure {
                println("아이템 패치 노트 읽어오기 실패 : $it")
            }
        }
    }
}