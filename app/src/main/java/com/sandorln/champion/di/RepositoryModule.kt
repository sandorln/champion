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
    @Binds
    @Singleton
    abstract fun providesChampionRepository(champRepoImpl: ChampionRepositoryImpl): ChampionRepository

    @Binds
    @Singleton
    abstract fun providesBoardRepository(boardRepoImpl: BoardRepositoryImpl): BoardRepository

    @Binds
    @Singleton
    abstract fun providesVersionRepository(versionRepoImpl: VersionRepositoryImpl): VersionRepository

    @Binds
    @Singleton
    abstract fun bindsItemRepository(itemRepositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindsSummonerSpellRepository(summonerSpellRepository: SummonerSpellRepositoryImpl): SummonerSpellRepository

    @Binds
    @Singleton
    abstract fun bindsAppSettingRepository(appSettingRepositoryImpl: AppSettingRepositoryImpl): AppSettingRepository
}