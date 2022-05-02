package com.sandorln.champion.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sandorln.champion.R
import com.sandorln.champion.di.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersionImage : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val requestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean = false

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            colorFilter = null
            this@VersionImage.setImageDrawable(resource)
            return true
        }
    }

    private fun createCircularProgressDrawable() = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        setColorFilter(ContextCompat.getColor(context, R.color.base))
        start()
    }


    fun setChampionThumbnail(championVersion: String, championId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/$championVersion/img/champion/${championId}.png")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .placeholder(createCircularProgressDrawable())
            .listener(requestListener)
            .into(this)
    }

    fun setSkillIcon(championVersion: String, skillImageName: String, isPassive: Boolean) {
        val path = if (isPassive) "passive" else "spell"
        val url = "http://ddragon.leagueoflegends.com/cdn/$championVersion/img/$path/${skillImageName}"

        GlideApp.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(createCircularProgressDrawable())
            .listener(requestListener)
            .into(this)
    }

    fun setItemThumbnail(itemVersion: String, itemId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/${itemVersion}/img/item/${itemId}.png")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(createCircularProgressDrawable())
            .listener(requestListener)
            .fitCenter()
            .into(this)
    }

    fun setSummonerSpellThumbnail(summonerSpellVersion: String, summonerSpellId: String) {
        GlideApp.with(this)
            .load("http://ddragon.leagueoflegends.com/cdn/${summonerSpellVersion}/img/spell/${summonerSpellId}.png")
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(createCircularProgressDrawable())
            .listener(requestListener)
            .fitCenter()
            .into(this)
    }
}