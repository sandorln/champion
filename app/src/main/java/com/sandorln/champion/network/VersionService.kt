package com.sandorln.champion.network

import com.sandorln.model.VersionLol
import retrofit2.http.GET

interface VersionService {
    /* 최신 버전 */
    @GET("/realms/na.json")
    suspend fun getVersion(): com.sandorln.model.VersionLol

    /* 최신 버전 & 이전 버전들까지 */
    @GET("/api/versions.json")
    suspend fun getVersionList(): List<String>
}