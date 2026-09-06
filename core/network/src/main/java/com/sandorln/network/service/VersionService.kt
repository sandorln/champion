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

import com.sandorln.network.util.getPatchNoteUrlCandidates

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
        val candidates = "$major1.$minor1.0".getPatchNoteUrlCandidates()
        for (url in candidates) {
            val isSuccess = runCatching {
                Jsoup.connect(url)
                    .timeout(5_000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .execute()
                    .statusCode() == 200
            }.getOrDefault(false)
            if (isSuccess) return@withContext url
        }
        return@withContext ""
    }
}