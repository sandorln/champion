package com.sandorln.champion.di

import com.sandorln.champion.repository.ChampionRepository
import com.sandorln.champion.repository.ItemRepository
import com.sandorln.champion.repository.VersionRepository
import com.sandorln.champion.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
class UseCaseModule {
    @Provides
    fun providesGetVersionCategory(versionRepo: VersionRepository): GetVersionCategory = GetVersionCategory(versionRepo)

    @Provides
    fun providesGetChampionList(getVersion: GetVersionCategory, champRepo: ChampionRepository): GetChampionList = GetChampionList(getVersion, champRepo)

    @Provides
    fun providesGetChampionInfo(getVersion: GetVersionCategory, champRepo: ChampionRepository): GetChampionInfo = GetChampionInfo(getVersion, champRepo)

    @Provides
    fun providesGetItemList(getVersion: GetVersionCategory, itemRepo: ItemRepository) = GetItemList(getVersion, itemRepo)

    @Provides
    fun providesFindItemById(getVersion: GetVersionCategory, itemRepo: ItemRepository) = FindItemById(getVersion, itemRepo)
}