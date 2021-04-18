package com.sandorln.champion.di

import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.network.VersionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    @Provides
    fun providesVersionManager(versionService: VersionService): VersionManager = VersionManager(versionService)
}