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
    private val ktorClient: HttpClient,
    private val fireDB: FirebaseFirestore
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

        val rating = getChampionRating(championName)

        response.data?.get(championName)?.copy(rating = rating) ?: throw Exception("")
    }

    suspend fun getChampionPathNoteList(version: String): List<NetworkChampionPatchNote> = withContext(Dispatchers.IO) {
        val (major1, minor1, _) = version
            .split('.')
            .map { it.toInt() }

        /* Version 이 10보다 낮을 시 패치노트가 존재하지 않음 */
        if (major1 < 10) return@withContext emptyList()

        val url = "https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$major1-$minor1-notes/"
        return@withContext Jsoup.connect(url).get().toNetworkChampionPatchNoteList()
    }

    suspend fun getChampionRating(championName: String): Float {
        val ratingList = fireDB
            .getLolDocument(FireStoreDocument.RATING)
            .collection(championName.lowercase())
            .get()
            .await()
            .documents
            .mapNotNull {
                runCatching { it.data?.get("rating") as Number }.getOrNull()
            }

        val totalRating = ratingList.sumOf { it.toDouble() }.toFloat()
        val totalCount = ratingList.size

        return if (totalCount > 0)
            runCatching { totalRating / totalCount }.getOrNull() ?: 0f
        else
            0f
    }

    suspend fun setChampionRating(championName: String, rating: Int) {
        val id = FirebaseInstallations.getInstance().getUserId()
        val data = mapOf("rating" to rating)

        fireDB
            .getLolDocument(FireStoreDocument.RATING)
            .collection(championName.lowercase())
            .document(id)
            .set(data)
            .await()
    }
}