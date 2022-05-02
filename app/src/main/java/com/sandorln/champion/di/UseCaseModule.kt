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
    fun providesGetChampionVersion(versionRepo: VersionRepository): GetChampionVersion = GetChampionVersion(versionRepo)

    @Provides
    fun providesGetChampionList(getChampionVersion: GetChampionVersion, champRepo: ChampionRepository): GetChampionList = GetChampionList(getChampionVersion, champRepo)

    @Provides
    fun providesGetChampionInfo(champRepo: ChampionRepository): GetChampionInfo = GetChampionInfo(champRepo)

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

    @Provides
    fun providesGetItemVersion(versionRepo: VersionRepository): GetItemVersion = GetItemVersion(versionRepo)

    @Provides
    fun providesGetSummonerSpellVersion(versionRepo: VersionRepository): GetSummonerSpellVersion = GetSummonerSpellVersion(versionRepo)

    @Provides
    fun providesHasNewLolVersion(getVersionList: GetVersionList, getVersion: GetVersion) = HasNewLolVersionUseCase(getVersionList, getVersion)

    @Provides
    fun providesChangeNewestLolVersion(getVersionList: GetVersionList, changeVersion: ChangeVersion) = ChangeNewestVersionUseCase(getVersionList, changeVersion)
}