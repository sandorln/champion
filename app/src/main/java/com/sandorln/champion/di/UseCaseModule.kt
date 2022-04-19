package com.sandorln.champion.di

import com.sandorln.champion.repository.ChampionRepository
import com.sandorln.champion.repository.VersionRepository
import com.sandorln.champion.use_case.GetChampionInfo
import com.sandorln.champion.use_case.GetChampionList
import com.sandorln.champion.use_case.GetVersionCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class UseCaseModule {
    @Provides
    fun providesGetChampionList(versionRepo: VersionRepository, champRepo: ChampionRepository): GetChampionList = GetChampionList(versionRepo, champRepo)

    @Provides
    fun providesGetChampionInfo(versionRepo: VersionRepository, champRepo: ChampionRepository): GetChampionInfo = GetChampionInfo(versionRepo, champRepo)

    @Provides
    fun providesGetVersionCategory(versionRepo: VersionRepository): GetVersionCategory = GetVersionCategory(versionRepo)
}