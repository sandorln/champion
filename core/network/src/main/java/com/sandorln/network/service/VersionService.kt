package com.sandorln.network.service

import com.sandorln.network.BuildConfig
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
        val oldUrl = "https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$major1-$minor1-notes/"
        val newUrl = "https://www.leagueoflegends.com/ko-kr/news/game-updates/lol-patch-$major1-$minor1-notes/"
        val oldUrlDeferred = async { runCatching { Jsoup.connect(oldUrl).get() } }
        val newUrlDeferred = async { runCatching { Jsoup.connect(newUrl).get() } }

        return@withContext when {
            oldUrlDeferred.await().isSuccess -> oldUrl
            newUrlDeferred.await().isSuccess -> newUrl
            else -> ""
        }
    }
}