package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.rune.NetworkRuneStyle
import com.sandorln.network.util.getPatchNoteUrl
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
class RuneService @Inject constructor(
    private val ktorClient: HttpClient
) {
    /**
     * 해당 버전의 룬 정보 가져오기
     */
    suspend fun getAllRuneDataList(version: String): List<NetworkRuneStyle> = withContext(
        Dispatchers.IO
    ) {
        ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/runesReforged.json")
            .body<List<NetworkRuneStyle>>()
    }

    suspend fun getRunePathNoteList(version: String): List<NetworkPatchNoteData> = withContext(Dispatchers.IO) {
        val runeResult = runCatching {
            Jsoup.connect(version.getPatchNoteUrl()).get().toNetworkPatchNoteList(NetworkPatchNoteType.Rune)
        }.getOrNull()

        return@withContext runeResult?.takeIf(List<NetworkPatchNoteData>::isNotEmpty) ?: emptyList()
    }
}