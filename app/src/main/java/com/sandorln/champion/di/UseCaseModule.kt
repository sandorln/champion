package com.sandorln.champion.di

import com.sandorln.champion.repository.ChampionRepository
import com.sandorln.champion.repository.ItemRepository
import com.sandorln.champion.repository.SummonerSpellRepository
import com.sandorln.champion.repository.VersionRepository
import com.sandorln.champion.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class UseCaseModule {
    @Provides
    fun providesGetVersionCategory(versionRepo: VersionRepository): GetVersionCategory = GetVersionCategory(versionRepo)

    @Provides
    fun providesGetChampionList(getVersion: GetVersion, champRepo: ChampionRepository): GetChampionList = GetChampionList(getVersion, champRepo)

    @Provides
    fun providesGetChampionInfo(getVersion: GetVersion, champRepo: ChampionRepository): GetChampionInfo = GetChampionInfo(getVersion, champRepo)

    @Provides
    fun providesGetItemList(getVersion: GetVersion, itemRepo: ItemRepository) = GetItemList(getVersion, itemRepo)

    @Provides
    fun providesFindItemById(getVersion: GetVersion, itemRepo: ItemRepository) = FindItemById(getVersion, itemRepo)

    @Provides
    fun providesGetSummonerSpellList(getVersion: GetVersion, summonerRepo: SummonerSpellRepository) = GetSummonerSpellList(getVersion, summonerRepo)

    @Provides
    fun providesGetVersion(versionRepo: VersionRepository): GetVersion = GetVersion(versionRepo)

    @Provides
    fun providesGetVersionList(versionRepo: VersionRepository): GetVersionList = GetVersionList(versionRepo)

    @Provides
    fun providesChangeVersion(versionRepo: VersionRepository): ChangeVersion = ChangeVersion(versionRepo)
}