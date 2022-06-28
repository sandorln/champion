package com.sandorln.champion.view.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
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
class VersionImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    val requestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean = false
        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            colorFilter = null
            this@VersionImage.setImageDrawable(resource)
            return true
        }
    }

    fun createCircularProgressDrawable() = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        setColorFilter(ContextCompat.getColor(context, R.color.base))
        start()
    }

    fun setChampionThumbnail(championVersion: String, championId: String) {
        val url = "http://ddragon.leagueoflegends.com/cdn/$championVersion/img/champion/${championId}.png"
        loadGlideApp(url)
    }

    fun setSkillIcon(championVersion: String, skillImageName: String, isPassive: Boolean) {
        val path = if (isPassive) "passive" else "spell"
        val url = "http://ddragon.leagueoflegends.com/cdn/$championVersion/img/$path/${skillImageName}"

        loadGlideApp(url)
    }

    fun setItemThumbnail(itemVersion: String, itemId: String) {
        val url = "http://ddragon.leagueoflegends.com/cdn/${itemVersion}/img/item/${itemId}.png"
        loadGlideApp(url)
    }

    fun setSummonerSpellThumbnail(summonerSpellVersion: String, summonerSpellId: String) {
        val url = "http://ddragon.leagueoflegends.com/cdn/${summonerSpellVersion}/img/spell/${summonerSpellId}.png"
        loadGlideApp(url)
    }

    private fun loadGlideApp(url: String) {
        GlideApp.with(this)
            .load(url)
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(createCircularProgressDrawable())
            .listener(requestListener)
            .fitCenter()
            .into(this)
    }
}

@BindingAdapter(value = ["championVersion", "championId"], requireAll = false)
fun VersionImage.setChampionThumbnailImage(championVersion: String, championId: String) {
    GlideApp.with(this)
        .load("http://ddragon.leagueoflegends.com/cdn/$championVersion/img/champion/${championId}.png")
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .fitCenter()
        .placeholder(createCircularProgressDrawable())
        .listener(requestListener)
        .into(this)
}