package com.sandorln.champion.model

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity
data class ChampionSkin(
    @SerializedName("id")
    var skId: String = "",
    @SerializedName("num")
    var skNum: String = "",
    @SerializedName("name")
    var skName: String = "",
    @SerializedName("chromas")
    var skChromas: Boolean = false
) : Serializable, Parcelable
