package com.sandorln.data.util

import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteData


fun NetworkPatchNoteData.asChampionData(): PatchNoteData = PatchNoteData(
    title = title,
    imageUrl = imageUrl,
    summary = summary.dropLast(1).replace(". ", "\n")
)

fun NetworkPatchNoteData.asItemData(): PatchNoteData = PatchNoteData(
    title = title,
    imageUrl = imageUrl,
    summary = summary.dropLast(1).replace(". ", "\n")
)