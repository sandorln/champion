package com.sandorln.network.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.sandorln.network.BuildConfig
import com.sandorln.network.model.FireStoreDocument
import com.sandorln.network.model.champion.NetworkChampion
import com.sandorln.network.model.champion.NetworkChampionDetail
import com.sandorln.network.model.champion.NetworkChampionPatchNote
import com.sandorln.network.model.response.BaseLolResponse
import com.sandorln.network.util.getLolDocument
import com.sandorln.network.util.getUserId
import com.sandorln.network.util.toNetworkChampionPatchNoteList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
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

    suspend fun getChampionPathNoteList(version: String): List<NetworkChampionPatchNote> = withContext(Dispatchers.IO) {
        val (major1, minor1, _) = version
            .split('.')
            .map { it.toInt() }

        /* Version 이 10보다 낮을 시 패치노트가 존재하지 않음 */
        if (major1 < 10) return@withContext emptyList()

        val url = if (major1 < 14 || (major1 == 14 && minor1 < 13))
            "https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$major1-$minor1-notes/"
        else
            "https://www.leagueoflegends.com/ko-kr/news/game-updates/lol-patch-$major1-$minor1-notes/"
        return@withContext Jsoup.connect(url).get().toNetworkChampionPatchNoteList()
    }
}