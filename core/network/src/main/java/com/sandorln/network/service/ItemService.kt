package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.item.NetworkItem
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


        val urlBuilder = StringBuilder("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-")
        when {
            major1 == 15 && (1..2).contains(minor1) -> urlBuilder.append("${major1 + 10}-s1-$minor1-notes/")
            major1 == 15 && 3 == minor1 -> urlBuilder.append("2025-s1-3-notes/")
            major1 >= 15 -> urlBuilder.append("${major1 + 10}-${minor1.toString().padStart(2, '0')}-notes/")
            else -> urlBuilder.append("$major1-$minor1-notes/")
        }

        val itemResult = runCatching {
            Jsoup.connect(urlBuilder.toString()).get().toNetworkPatchNoteList(NetworkPatchNoteType.Item)
        }.getOrNull()

        return@withContext itemResult?.takeIf(List<NetworkPatchNoteData>::isNotEmpty) ?: emptyList()
    }
}