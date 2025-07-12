package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.champion.NetworkChampion
import com.sandorln.network.model.champion.NetworkChampionDetail
import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.response.BaseLolResponse
import com.sandorln.network.util.toNetworkPatchNoteList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChampionService @Inject constructor(
    private val ktorClient: HttpClient
) {
    /**
     * 해당 버전의 간략한 챔피온 정보 가져오기
     * @return Key : 해당 챔피언 고유 Key Value
     */
    suspend fun getAllChampionDataMap(version: String): Map<String, NetworkChampion> = withContext(Dispatchers.IO) {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/champion.json")
            .body<BaseLolResponse<Map<String, NetworkChampion>>>()

        response.data ?: throw Exception("")
    }

    suspend fun getChampionDetail(version: String, championName: String): NetworkChampionDetail = withContext(Dispatchers.IO) {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/ko_KR/champion/${championName}.json")
            .body<BaseLolResponse<Map<String, NetworkChampionDetail>>>()

        response.data?.get(championName) ?: throw Exception("")
    }

    suspend fun getChampionPathNoteList(version: String): List<NetworkPatchNoteData> = withContext(Dispatchers.IO) {
        val (major1, minor1, _) = version
            .split('.')
            .map { it.toInt() }

        if (major1 < 10) return@withContext emptyList()

        val urlBuilder = StringBuilder("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-")
        when {
            major1 == 15 && (1..2).contains(minor1) -> urlBuilder.append("${major1 + 10}-s1-$minor1-notes/")
            major1 == 15 && 3 == minor1 -> urlBuilder.append("2025-s1-3-notes/")
            major1 >= 15 -> urlBuilder.append("${major1 + 10}-${minor1.toString().padStart(2, '0')}-notes/")
            else -> urlBuilder.append("$major1-$minor1-notes/")
        }

        val championResult = runCatching {
            Jsoup.connect(urlBuilder.toString()).get().toNetworkPatchNoteList(NetworkPatchNoteType.Champion)
        }.getOrNull()

        return@withContext championResult?.takeIf(List<NetworkPatchNoteData>::isNotEmpty) ?: emptyList()
    }
}