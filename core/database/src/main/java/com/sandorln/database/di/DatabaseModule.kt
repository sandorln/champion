package com.sandorln.database.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.sandorln.database.AppDatabase
import com.sandorln.database.converter.BitmapConverters
import com.sandorln.database.converter.LolChampionConverters
import com.sandorln.database.converter.LolItemConverters
import com.sandorln.database.dao.ChampionDao
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.dao.SpriteImageDao
import com.sandorln.database.dao.SummonerSpellDao
import com.sandorln.database.dao.VersionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    private const val DB_NAME = "lol-champion"

    @Singleton
    @Provides
    fun providesGson(): Gson = Gson()

    @Provides
    @Singleton
    fun providesAppDatabase(
        @ApplicationContext context: Context,
        lolChampionConverters: LolChampionConverters,
        lolItemConverters: LolItemConverters,
        bitmapConverters: BitmapConverters
    ): AppDatabase = Room
        .databaseBuilder(context, AppDatabase::class.java, DB_NAME)
        .fallbackToDestructiveMigration()
        .addTypeConverter(lolChampionConverters)
        .addTypeConverter(lolItemConverters)
        .addTypeConverter(bitmapConverters)
        .build()

    @Provides
    @Singleton
    fun providesChampionDao(appDatabase: AppDatabase): ChampionDao = appDatabase.championDao()

    @Provides
    @Singleton
    fun providesItemDao(appDatabase: AppDatabase): ItemDao = appDatabase.itemDao()

    @Provides
    @Singleton
    fun providesSummonerSpellDao(appDatabase: AppDatabase): SummonerSpellDao = appDatabase.summonerSpellDao()

    @Provides
    @Singleton
    fun providesVersionDao(appDatabase: AppDatabase): VersionDao = appDatabase.versionDao()

    @Provides
    @Singleton
    fun providesSpriteImageDao(appDatabase: AppDatabase): SpriteImageDao = appDatabase.spriteImageDao()
}