package com.sandorln.champion.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.manager.VersionManager

fun ImageView.setChampionThumbnail(championId: String) {
    val version = VersionManager.getVersion(this.context).category.champion
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/$version/img/champion/${championId}.png")
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .into(this)
}

fun ImageView.setChampionSplash(championId: String, skinNum: String) {
    Glide.with(context)
        .load("http://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_${skinNum}.jpg")
        .thumbnail(0.5f)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.setSkillIcon(skillImageName: String, isPassive: Boolean) {
    val path = if (isPassive) "passive" else "spell"
    val version = VersionManager.getVersion(this.context).category.champion
    val url = "http://ddragon.leagueoflegends.com/cdn/$version/img/$path/${skillImageName}"

    Glide.with(context)
        .load(url)
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}