package com.sandorln.champion.view.binding

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.R


fun ImageView.setCharacterThumbnail(version: String, championId: String) {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/$version/img/champion/${championId}.png")
        .placeholder(R.drawable.ic_launcher_foreground)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .into(this)
}