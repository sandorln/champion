package com.sandorln.data.di

import com.sandorln.data.repo.version.DefaultVersionRepository
import com.sandorln.data.repo.version.VersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindsVersionRepository(defaultVersionRepository: DefaultVersionRepository): VersionRepository
}