package com.sandorln.domain.usecase.version

import com.sandorln.data.repository.version.VersionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLolPatchNoteUrl @Inject constructor(
    private val versionRepository: VersionRepository
) {
    suspend operator fun invoke(major1 : Int, minor1 : Int) = versionRepository.getLolPatchNoteUrl(major1, minor1)
}