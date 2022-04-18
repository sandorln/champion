package com.sandorln.champion.di

import com.sandorln.champion.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun providesChampionRepository(championRepositoryImpl: ChampionRepositoryImpl): ChampionRepository

    @Singleton
    @Binds
    abstract fun providesBoardRepository(boardRepositoryImpl: BoardRepositoryImpl): BoardRepository

    @Singleton
    @Binds
    abstract fun providesVersionRepository(versionRepositoryImpl: VersionRepositoryImpl): VersionRepository
}