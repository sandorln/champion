package com.sandorln.model.exception

sealed class SpriteException(message: String) : Exception(message) {
    data object NotFoundVersionFolder : SpriteException("초기화가 이뤄지지 않았습니다")
    data object NotFoundSpriteFileInFolder : SpriteException("초기화가 이뤄지지 않았습니다")
}