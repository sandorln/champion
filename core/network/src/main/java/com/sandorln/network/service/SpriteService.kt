package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpriteService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getSpriteFile(version: String, fileName: String): InputStream =
        ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/img/sprite/${fileName}")
            .bodyAsChannel()
            .toInputStream()
}