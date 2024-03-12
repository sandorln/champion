package com.sandorln.network.di

import android.util.Log
import com.sandorln.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun providesKtorHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        if (BuildConfig.DEBUG) {
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("serverConnect", message)
                    }
                }

                level = LogLevel.ALL
            }
        }
    }
}