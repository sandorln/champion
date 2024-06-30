package com.sandorln.domain.usecase.game

import com.sandorln.data.repository.game.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRefreshRankGameNextTime @Inject constructor(
    private val gameRepository: GameRepository
) {
    operator fun invoke() = gameRepository.refreshRankGameNextTime
}