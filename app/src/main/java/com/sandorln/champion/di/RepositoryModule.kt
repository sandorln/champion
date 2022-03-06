package com.sandorln.champion.di

import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.network.ChampionService
import com.sandorln.champion.repository.BoardRepository
import com.sandorln.champion.repository.ChampionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun providesChampionRepository(championService: ChampionService, versionManager: VersionManager): ChampionRepository =
        ChampionRepository(championService, versionManager)

    @Provides
    fun providesBoardRepository(): BoardRepository = BoardRepository()
}