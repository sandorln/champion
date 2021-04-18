package com.sandorln.champion.di

import com.sandorln.champion.BuildConfig
import com.sandorln.champion.network.ChampionService
import com.sandorln.champion.network.VersionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesHttpLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    fun providesClient(loggerInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG)
                addInterceptor(loggerInterceptor)
        }.build()

    @Provides
    fun providesRetrofit(client: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun providesChampionService(retrofit: Retrofit): ChampionService = retrofit.create(ChampionService::class.java)
    @Provides
    fun providesVersionService(retrofit: Retrofit): VersionService = retrofit.create(VersionService::class.java)
}