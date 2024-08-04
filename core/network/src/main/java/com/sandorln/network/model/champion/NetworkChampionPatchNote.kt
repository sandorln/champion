package com.sandorln.network.model.champion

data class NetworkChampionPatchNote(
    val title: String,
    val image: String,
    val summary: String,
    val detailPathStory: String
)

data class NetworkItemPatchNote(
    val itemName: String,
    val image: String,
    val detailPathList: List<String>
)