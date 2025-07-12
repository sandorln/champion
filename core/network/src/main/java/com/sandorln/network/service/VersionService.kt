package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getVersionList(): List<String> = withContext(Dispatchers.IO) {
        ktorClient
            .get(BuildConfig.BASE_URL + "/api/versions.json")
            .body<List<String>>()
            .filter { !it.startsWith("lolpatch") }
    }

    suspend fun getLolPatchNoteUrl(major1: Int, minor1: Int): String = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-")
        when {
            major1 == 15 && (1..2).contains(minor1) -> urlBuilder.append("${major1 + 10}-s1-$minor1-notes/")
            major1 == 15 && 3 == minor1 -> urlBuilder.append("2025-s1-3-notes/")
            major1 >= 15 -> urlBuilder.append("${major1 + 10}-${minor1.toString().padStart(2, '0')}-notes/")
            else -> urlBuilder.append("$major1-$minor1-notes/")
        }

        val isSuccess = runCatching { Jsoup.connect(urlBuilder.toString()).get() }.isSuccess
        return@withContext if (isSuccess) urlBuilder.toString() else ""
    }
}