package com.sandorln.champion.di

import com.sandorln.champion.repository.*
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
    fun providesGetChampionList(
        getChampionVersionUseCase: GetChampionVersionUseCase,
        champRepo: ChampionRepository,
        getLanguageCodeUseCase: GetLanguageCodeUseCase
    ): GetChampionListUseCase =
        GetChampionListUseCase(
            getChampionVersionUseCase = getChampionVersionUseCase,
            getLanguageCodeUseCase = getLanguageCodeUseCase,
            championRepository = champRepo
        )

    @Provides
    fun providesGetChampionInfo(champRepo: ChampionRepository, getLanguageCodeUseCase: GetLanguageCodeUseCase): GetChampionInfoUseCase =
        GetChampionInfoUseCase(champRepo, getLanguageCodeUseCase)

    @Provides
    fun providesGetItemList(
        getVersionUseCase: GetVersionUseCase,
        getLanguageCodeUseCase: GetLanguageCodeUseCase,
        itemRepo: ItemRepository
    ) = GetItemListUseCase(
        getVersionUseCase = getVersionUseCase,
        itemRepository = itemRepo,
        getLanguageCodeUseCase = getLanguageCodeUseCase
    )

    @Provides
    fun providesFindItemById(
        getVersionUseCase: GetVersionUseCase,
        getLanguageCodeUseCase: GetLanguageCodeUseCase,
        itemRepo: ItemRepository,
    ) = FindItemByIdUseCase(
        getVersionUseCase = getVersionUseCase,
        itemRepository = itemRepo,
        getLanguageCodeUseCase = getLanguageCodeUseCase
    )

    @Provides
    fun providesGetSummonerSpellList(
        getVersionUseCase: GetVersionUseCase,
        getLanguageCodeUseCase: GetLanguageCodeUseCase,
        summonerRepo: SummonerSpellRepository
    ) = GetSummonerSpellListUseCase(
        getVersionUseCase = getVersionUseCase,
        summonerSpellRepository = summonerRepo,
        getLanguageCodeUseCase = getLanguageCodeUseCase
    )

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

    @Provides
    fun providesGetAppSetting(appSettingRepository: AppSettingRepository) = GetAppSettingUseCase(appSettingRepository)

    @Provides
    fun providesToggleAppSetting(appSettingRepository: AppSettingRepository) = ToggleAppSettingUseCase(appSettingRepository)

    @Provides
    fun providesGetLanguageCode(languageRepository: LanguageRepository) = GetLanguageCodeUseCase(languageRepository)
}