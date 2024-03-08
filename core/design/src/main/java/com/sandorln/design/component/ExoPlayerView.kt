package com.sandorln.design.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sandorln.design.theme.Colors

@Composable
fun ExoPlayerView(
    modifier: Modifier = Modifier,
    url: String = ""
) {
    val context = LocalContext.current
    val mediaSource = remember(url) {
        MediaItem.fromUri(Uri.parse(url))
    }

    val exoPlayer = remember(mediaSource) {
        ExoPlayer
            .Builder(context)
            .build()
            .apply {
                setMediaItem(mediaSource)
                prepare()
                playWhenReady = true
            }
    }

    Box(
        modifier = modifier.background(color = Colors.Gray08)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.matchParentSize(),
            update = {
                it.player?.release()
                it.player = null
                it.player = exoPlayer
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}