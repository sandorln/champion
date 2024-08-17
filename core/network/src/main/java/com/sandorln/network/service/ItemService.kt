package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.item.NetworkItem
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.response.BaseLolResponse
import com.sandorln.network.util.toNetworkPatchNoteList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getAllItemMap(version: String): Map<String, NetworkItem> = withContext(Dispatchers.IO) {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/item.json")
            .body<BaseLolResponse<Map<String, NetworkItem>>>()

        response.data ?: throw Exception("")
    }

    suspend fun getItemPathNoteList(version: String): List<NetworkPatchNoteData> = withContext(Dispatchers.IO) {
        val (major1, minor1, _) = version
            .split('.')
            .map { it.toInt() }

        if (major1 < 10) return@withContext emptyList()

        val oldPatchUrl = async {
            runCatching {
                val url = "https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$major1-$minor1-notes/"
                Jsoup.connect(url).get().toNetworkPatchNoteList(NetworkPatchNoteType.Item)
            }
        }
        val newPatchUrl = async {
            runCatching {
                val url = "https://www.leagueoflegends.com/ko-kr/news/game-updates/lol-patch-$major1-$minor1-notes/"
                Jsoup.connect(url).get().toNetworkPatchNoteList(NetworkPatchNoteType.Item)
            }
        }

        val oldUrlItemResult = oldPatchUrl.await().getOrNull()
        val newUrlItemResult = newPatchUrl.await().getOrNull()
        val finalItemPatchList = oldUrlItemResult?.takeIf(List<NetworkPatchNoteData>::isNotEmpty) ?: newUrlItemResult ?: emptyList()

        return@withContext finalItemPatchList
    }
}