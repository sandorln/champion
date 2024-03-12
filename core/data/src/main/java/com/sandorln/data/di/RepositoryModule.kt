package com.sandorln.data.di

import android.content.Context
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.champion.DefaultChampionRepository
import com.sandorln.data.repository.item.DefaultItemRepository
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.spell.DefaultSummonerSpellRepository
import com.sandorln.data.repository.spell.SummonerSpellRepository
import com.sandorln.data.repository.sprite.DefaultSpriteRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.DefaultVersionRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindsVersionRepository(defaultVersionRepository: DefaultVersionRepository): VersionRepository

    @Binds
    @Singleton
    fun bindsSpriteRepository(defaultSpriteRepository: DefaultSpriteRepository): SpriteRepository

    @Binds
    @Singleton
    fun bindsChampionRepository(defaultChampionRepository: DefaultChampionRepository): ChampionRepository

    @Binds
    @Singleton
    fun bindsItemRepository(defaultItemRepository: DefaultItemRepository): ItemRepository

    @Binds
    @Singleton
    fun bindsSummonerSpellRepository(defaultSummonerSpellRepository: DefaultSummonerSpellRepository): SummonerSpellRepository
}