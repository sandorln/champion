package com.sandorln.domain.usecase.itembuild

import com.sandorln.data.repository.item.ItemBuildRepository
import com.sandorln.model.data.item.ItemBuild
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveItemBuild @Inject constructor(
    private val itemBuildRepository: ItemBuildRepository
) {
    suspend operator fun invoke(itemBuild: ItemBuild): Result<Long> = runCatching {
        if (itemBuild.id == 0L) {
            val currentCount = itemBuildRepository.getItemBuildCountByVersion(itemBuild.version)
            if (currentCount >= 10) {
                throw IllegalStateException("버전당 최대 10개의 빌드만 생성할 수 있습니다.")
            }
        }
        itemBuildRepository.saveItemBuild(itemBuild)
    }
}
