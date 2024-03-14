package com.sandorln.model.result

sealed class ResultData<out T> {
    data class Success<T>(val data: T?) : ResultData<T>()
    data class Failed<T>(val exception: Exception, val data: T? = null) : ResultData<T>()
    data object Loading : ResultData<Nothing>()
}
