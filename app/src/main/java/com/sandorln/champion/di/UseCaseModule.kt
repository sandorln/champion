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
    fun providesGetChampionList(getVersionCategory: GetVersionCategory, champRepo: ChampionRepository): GetChampionList = GetChampionList(getVersionCategory, champRepo)

    @Provides
    fun providesGetChampionInfo(getVersionCategory: GetVersionCategory, champRepo: ChampionRepository): GetChampionInfo = GetChampionInfo(getVersionCategory, champRepo)

    @Provides
    fun providesGetVersionCategory(versionRepo: VersionRepository): GetVersionCategory = GetVersionCategory(versionRepo)
}