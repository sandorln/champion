package com.sandorln.network.model.item

data class NetworkItemPatchNote(
    val itemName: String,
    val image: String,
    val detailPathList: List<String>
)