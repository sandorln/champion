package com.sandorln.datastore.di

import com.sandorln.datastore.appsetting.AppSettingDatasource
import com.sandorln.datastore.appsetting.DefaultAppSettingDatasource
import com.sandorln.datastore.version.DefaultVersionDatasource
import com.sandorln.datastore.version.VersionDatasource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface DatasourceModule {
    @Binds
    @Singleton
    fun bindsAppSettingDatasource(defaultAppSettingDatasource: DefaultAppSettingDatasource): AppSettingDatasource

    @Binds
    @Singleton
    fun bindsVersionDatasource(versionDatasource: DefaultVersionDatasource): VersionDatasource
}