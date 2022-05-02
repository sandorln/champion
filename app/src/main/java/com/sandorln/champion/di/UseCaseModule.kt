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
    fun providesGetChampionVersion(versionRepo: VersionRepository): GetChampionVersionUseCase = GetChampionVersionUseCase(versionRepo)

    @Provides
    fun providesGetChampionList(getChampionVersionUseCase: GetChampionVersionUseCase, champRepo: ChampionRepository): GetChampionListUseCase = GetChampionListUseCase(getChampionVersionUseCase, champRepo)

    @Provides
    fun providesGetChampionInfo(champRepo: ChampionRepository): GetChampionInfoUseCase = GetChampionInfoUseCase(champRepo)

    @Provides
    fun providesGetItemList(getVersionUseCase: GetVersionUseCase, itemRepo: ItemRepository) = GetItemListUseCase(getVersionUseCase, itemRepo)

    @Provides
    fun providesFindItemById(getVersionUseCase: GetVersionUseCase, itemRepo: ItemRepository) = FindItemByIdUseCase(getVersionUseCase, itemRepo)

    @Provides
    fun providesGetSummonerSpellList(getVersionUseCase: GetVersionUseCase, summonerRepo: SummonerSpellRepository) = GetSummonerSpellListUseCase(getVersionUseCase, summonerRepo)

    @Provides
    fun providesGetVersion(versionRepo: VersionRepository): GetVersionUseCase = GetVersionUseCase(versionRepo)

    @Provides
    fun providesGetVersionList(versionRepo: VersionRepository): GetVersionListUseCase = GetVersionListUseCase(versionRepo)

    @Provides
    fun providesChangeVersion(versionRepo: VersionRepository): ChangeVersionUseCase = ChangeVersionUseCase(versionRepo)

    @Provides
    fun providesGetItemVersion(versionRepo: VersionRepository): GetItemVersionUseCase = GetItemVersionUseCase(versionRepo)

    @Provides
    fun providesGetSummonerSpellVersion(versionRepo: VersionRepository): GetSummonerSpellVersionUseCase = GetSummonerSpellVersionUseCase(versionRepo)
}