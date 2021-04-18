package com.sandorln.champion.network

import com.sandorln.champion.model.VersionLol
import retrofit2.http.GET

interface VersionService {
    @GET("/realms/na.json")
    suspend fun getVersion(): VersionLol
}