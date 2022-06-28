package com.sandorln.champion.database.shareddao

interface LanguageDao {
    fun insertLanguages(languages : List<String>)
    fun getLanguages() : List<String>
}