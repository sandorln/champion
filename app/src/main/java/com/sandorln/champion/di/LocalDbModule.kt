package com.sandorln.champion.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.sandorln.champion.database.AppDatabase
import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.database.shareddao.VersionDaoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalDbModule {
    /* Room */
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "lol-champion")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesChampionDao(appDatabase: AppDatabase): ChampionDao = appDatabase.championDao()

    /* SharedPreference */
    @Singleton
    @Provides
    fun providesSharedPreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("champion", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesSharedDao(sharedPreferences: SharedPreferences,gson: Gson): VersionDao = VersionDaoImpl(sharedPreferences,gson)

    @Singleton
    @Provides
    fun providesGson(): Gson = Gson()
}