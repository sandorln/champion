package com.sandorln.champion.di

import com.google.gson.Gson
import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.network.ChampionService
import com.sandorln.champion.network.VersionService
import com.sandorln.champion.repository.BoardRepository
import com.sandorln.champion.repository.ChampionRepository
import com.sandorln.champion.repository.VersionRepository
import com.sandorln.champion.repository.VersionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesChampionRepository(championService: ChampionService, championDao: ChampionDao): ChampionRepository = ChampionRepository(championService, championDao)

    @Singleton
    @Provides
    fun providesBoardRepository(): BoardRepository = BoardRepository()

    @Singleton
    @Provides
    fun providesVersionRepository(versionDao: VersionDao, versionService: VersionService): VersionRepository = VersionRepositoryImpl(versionDao, versionService)
}