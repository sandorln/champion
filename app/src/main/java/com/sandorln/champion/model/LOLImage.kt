package com.sandorln.champion.model

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity
data class LOLImage(
    var full: String = "",
    var sprite: String = "",
    var group: String = "",
    /* 이미지 좌표 */
    var x: Int = 0,
    var y: Int = 0,
    var w: Int = 0,
    var h: Int = 0
) : Serializable, Parcelable