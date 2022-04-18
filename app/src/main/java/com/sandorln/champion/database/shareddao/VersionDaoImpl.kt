package com.sandorln.champion.database.shareddao

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.sandorln.champion.model.VersionCategory

class VersionDaoImpl(
    private val pref: SharedPreferences,
    private val gson: Gson
) : VersionDao {
    companion object {
        private const val KEY_VERSION_CATEGORY = "key_version_category"
    }

    override fun insertVersionCategory(versionCategory: VersionCategory) {
        pref.edit(commit = true) {
            putString(KEY_VERSION_CATEGORY, gson.toJson(versionCategory))
        }
    }

    override fun getVersionCategory(): VersionCategory =
        gson.fromJson(pref.getString(KEY_VERSION_CATEGORY, ""), VersionCategory::class.java)
}