package com.sandorln.champion.tile.service

import android.content.Context
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import com.sandorln.champion.R
import com.sandorln.champion.tile.MessagingTileTheme

@OptIn(ExperimentalHorologistApi::class)
class ChampionRender(context: Context) : SingleTileLayoutRenderer<Any, Any>(context) {
    override fun renderTile(state: Any, deviceParameters: DeviceParametersBuilders.DeviceParameters): LayoutElementBuilders.LayoutElement =
        MultiButtonLayout
            .Builder()
            .addButtonContent(
                Button
                    .Builder(
                        context,
                        ModifiersBuilders.Clickable.Builder().build()
                    )
                    .setContentDescription("asdonpoq")
                    .setImageContent("ic_search")
                    .build()
            )
            .addButtonContent(
                Button
                    .Builder(
                        context,
                        ModifiersBuilders.Clickable.Builder().build()
                    )
                    .setTextContent("안녕하세요")
                    .setIconContent("ic_search")
                    .setButtonColors(ButtonColors.secondaryButtonColors(MessagingTileTheme.colors))
                    .build()
            )
            .build()

    override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
        resourceState: Any,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        addIdToImageMapping(
            "ic_search",
            ResourceBuilders.ImageResource
                .Builder()
                .setAndroidResourceByResId(
                    ResourceBuilders.AndroidImageResourceByResId.Builder()
                        .setResourceId(R.drawable.img_error)
                        .build()
                )
                .build()
        )
    }
}