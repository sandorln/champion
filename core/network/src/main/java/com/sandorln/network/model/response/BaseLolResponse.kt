package com.sandorln.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseLolResponse<T>(
    val type: String = "",
    val version: String = "",

    val data: T? = null
)