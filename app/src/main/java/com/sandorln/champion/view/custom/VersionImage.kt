package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.di.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersionImage : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setChampionThumbnail(championVersion: String, championId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/$championVersion/img/champion/${championId}.png")
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(this)
    }

    fun setSkillIcon(championVersion: String, skillImageName: String, isPassive: Boolean) {
        val path = if (isPassive) "passive" else "spell"
        val url = "http://ddragon.leagueoflegends.com/cdn/$championVersion/img/$path/${skillImageName}"

        GlideApp.with(this)
            .load(url)
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }

    fun setItemThumbnail(itemVersion: String, itemId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/${itemVersion}/img/item/${itemId}.png")
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(this)
    }

    fun setSummonerSpellThumbnail(summonerSpellVersion: String, summonerSpellId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/${summonerSpellVersion}/img/spell/${summonerSpellId}.png")
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(this)
    }
}