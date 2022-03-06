package com.sandorln.champion.util

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.sandorln.champion.model.type.SpellType

fun ExoPlayer.playChampionSkill(championId: String, spellType: SpellType) {
    val mediaItem =
        if (spellType == SpellType.P)
            MediaItem.fromUri(Uri.parse("https://d28xe8vt774jo5.cloudfront.net/champion-abilities/${championId}/ability_${championId}_${spellType.name}1.mp4"))
        else
            MediaItem.fromUri(Uri.parse("https://d28xe8vt774jo5.cloudfront.net/champion-abilities/${championId}/ability_${championId}_${spellType.name}1.webm"))
    setMediaItem(mediaItem)
    prepare()
}