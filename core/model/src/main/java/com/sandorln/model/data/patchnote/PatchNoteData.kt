package com.sandorln.model.data.patchnote

data class PatchNoteData(
    val title: String,
    val imageUrl: String,
    val summary: String
) {
    val id: String get() = imageUrl.split("/").lastOrNull()?.split(".")?.firstOrNull() ?: ""
}