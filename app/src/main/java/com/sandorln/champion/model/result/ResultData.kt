package com.sandorln.champion.model.result

sealed class ResultData<out T> {
    data class Success<T>(val data: T?) : ResultData<T>()
    data class Failed(val exception: Exception) : ResultData<Nothing>()
    object Loading : ResultData<Nothing>()
}
