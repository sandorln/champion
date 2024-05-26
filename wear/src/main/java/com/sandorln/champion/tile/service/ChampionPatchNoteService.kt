package com.sandorln.champion.tile.service

import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.sandorln.champion.R
import com.sandorln.champion.tile.MessagingTileTheme

class ChampionPatchNoteService : TileService() {
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<Tile> =
        Futures.immediateFuture(
            Tile.Builder()
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        MultiButtonLayout
                            .Builder()
                            .addButtonContent(
                                Button
                                    .Builder(baseContext, Clickable.Builder().build())
                                    .setContentDescription("asdonpoq")
                                    .setImageContent("ic_search")
                                    .setButtonColors(ButtonColors.secondaryButtonColors(MessagingTileTheme.colors))
                                    .build()
                            )
                            .build()
                    )
                ).build()
        )

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<Resources> =
        Futures.immediateFuture(
            Resources.Builder()
                .setVersion("1")
                .addIdToImageMapping(
                    "ic_search",
                    ImageResource
                        .Builder()
                        .setAndroidResourceByResId(
                            AndroidImageResourceByResId.Builder()
                                .setResourceId(R.drawable.ic_search_24)
                                .build()
                        ).build()
                )
                .build()
        )
}