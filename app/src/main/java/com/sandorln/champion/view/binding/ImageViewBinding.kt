package com.sandorln.champion.view.binding

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.model.ChampionData


fun ImageView.setChampionThumbnail(version: String, championId: String) {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/$version/img/champion/${championId}.png")
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .into(this)
}

fun ImageView.setToolbarChampionThumbnail(version: String, championId: String) {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/$version/img/champion/${championId}.png")
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.setChampionSplash(championData: ChampionData) {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championData.cId}_${championData.cSkins.first().skNum}.jpg")
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.setChampionSkin(championId: String, skinNumber: String = "0") {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_${skinNumber}.jpg")
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}