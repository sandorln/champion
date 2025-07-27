package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.rune.NetworkRuneStyle
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
}