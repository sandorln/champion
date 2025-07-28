package com.sandorln.data.repository.rune

import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.data.rune.RuneStyle
import kotlinx.coroutines.flow.Flow

interface RuneRepository {
    val currentRuneStyleList: Flow<List<RuneStyle>>

    suspend fun refreshRuneStyleList(version: String): Result<Any>

    suspend fun getRunePatchList(version: String): List<PatchNoteData>
}