package com.example.bliblihomepage.network

import com.example.bliblihomepage.model.BannerResponse
import retrofit2.http.GET

interface BannerApiService {

    @GET("backend/content-api/pages/home2023/_without-content?display=mobile")
    suspend fun getBanners(): BannerResponse
}
