package com.sandorln.champion.database.shareddao

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LanguageDaoImpl(
    private val pref: SharedPreferences,
    private val gson: Gson
) : LanguageDao {
    companion object {
        private const val KEY_LANGUAGE = "key_language"
    }

    override fun insertLanguages(languages: List<String>) {
        pref.edit(commit = true) {
            putString(KEY_LANGUAGE, gson.toJson(languages))
        }
    }

    override fun getLanguages(): List<String> = try {
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(pref.getString(KEY_LANGUAGE, ""), type)
    } catch (e: Exception) {
        mutableListOf()
    }
}