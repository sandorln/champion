package com.sandorln.champion.view.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandorln.champion.database.shareddao.VersionDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VersionImage : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Inject
    lateinit var versionDao: VersionDao

    fun setChampionThumbnail(championId: String) {
        val version = versionDao.getVersionCategory().champion
        Glide.with(context)
            .load("http://ddragon.leagueoflegends.com/cdn/$version/img/champion/${championId}.png")
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(this)
    }

    fun setSkillIcon(skillImageName: String, isPassive: Boolean) {
        val path = if (isPassive) "passive" else "spell"
        val version = versionDao.getVersionCategory().champion
        val url = "http://ddragon.leagueoflegends.com/cdn/$version/img/$path/${skillImageName}"

        Glide.with(context)
            .load(url)
            .thumbnail(0.5f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }
}