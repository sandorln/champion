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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
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
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesChampionService(retrofit: Retrofit): ChampionService = retrofit.create(ChampionService::class.java)

    @Provides
    @Singleton
    fun providesVersionService(retrofit: Retrofit): VersionService = retrofit.create(VersionService::class.java)
}