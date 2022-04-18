package com.sandorln.champion.di

import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.network.ChampionService
import com.sandorln.champion.repository.BoardRepository
import com.sandorln.champion.repository.ChampionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    fun providesChampionRepository(championService: ChampionService, championDao: ChampionDao): ChampionRepository = ChampionRepository(championService, championDao)

    @Provides
    fun providesBoardRepository(): BoardRepository = BoardRepository()
}