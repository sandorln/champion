package com.sandorln.champion.util

import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.di.GlideApp

fun ImageView.setChampionSplash(championId: String, skinNum: String) {
    GlideApp.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_${skinNum}.jpg")
        .thumbnail(0.5f)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}