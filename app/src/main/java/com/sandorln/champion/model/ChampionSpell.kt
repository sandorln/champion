package com.sandorln.champion.model

import android.os.Parcelable
import androidx.room.Entity
import com.sandorln.champion.model.type.SpellType
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity
data class ChampionSpell(
    var id: String = "P",
    var name: String = "",
    var description: String = "",
    var image: LOLImage = LOLImage()
) : Serializable, Parcelable {
    fun getSpellType(position: Int): SpellType =
        try {
            SpellType.values()[position]
        } catch (e: Exception) {
            SpellType.P
        }
}