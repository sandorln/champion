package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getVersionList(): List<String> =
        ktorClient
            .get(BuildConfig.BASE_URL + "/api/versions.json")
            .body<List<String>>()
            .filter { !it.startsWith("lolpatch") }
}