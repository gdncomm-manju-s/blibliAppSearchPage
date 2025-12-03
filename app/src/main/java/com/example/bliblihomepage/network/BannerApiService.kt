package com.example.bliblihomepage.network

import com.example.bliblihomepage.model.BannerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BannerApiService {

    @GET("backend/content-api/pages/home2023/_without-content")
    suspend fun getBanners(
        @Query("display") display: String = "mobile"
    ): BannerResponse

}
