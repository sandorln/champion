package com.sandorln.champion.tile.service

import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService

@OptIn(ExperimentalHorologistApi::class)
class SuspendPathService : SuspendingTileService() {
    private lateinit var renderer: ChampionRender

    override fun onCreate() {
        super.onCreate()
        renderer = ChampionRender(this)
    }

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        renderer.produceRequestedResources(Any(), requestParams)

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile =
        renderer.renderTimeline(Any(), requestParams)
}