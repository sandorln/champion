package com.sandorln.core.network

import com.sandorln.network.service.ChampionService
import com.sandorln.network.service.ItemService
import com.sandorln.network.service.SummonerSpellService
import com.sandorln.network.service.VersionService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
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
    private lateinit var _championService: ChampionService
    private lateinit var _itemService: ItemService
    private lateinit var _summonerSpellService: SummonerSpellService

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

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        _versionService = VersionService(ktorClient)
        _championService = ChampionService(ktorClient)
        _itemService = ItemService(ktorClient)
        _summonerSpellService = SummonerSpellService(ktorClient)
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
}