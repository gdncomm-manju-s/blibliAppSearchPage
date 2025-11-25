package com.example.bliblihomepage.network

import com.example.bliblihomepage.model.ProductSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    // Real endpoint path
    @GET("backend/search/products")
    suspend fun searchProducts(
        @Query("searchTerm") searchTerm: String,
        @Query("page") page: Int = 1,
        @Query("start") start: Int = 0,
        @Query("merchantSearch") merchantSearch: Boolean = true,
        @Query("multiCategory") multiCategory: Boolean = true,
        @Query("intent") intent: Boolean = true,
        @Query("channelId") channelId: String = "mobile-web",
        @Query("showFacet") showFacet: Boolean = false,
        @Query("userIdentifier") userIdentifier: String = "555631665",
        @Query("isMobileBCA") isMobileBCA: Boolean = false,
        @Query("isJual") isJual: Boolean = false,
        @Query("userLatLong") userLatLong: String = "-6.195180099999999,106.8204412",
        @Query("userLocationCity") userLocationCity: String = "Kota Jakarta Pusat",
        @Query("userLocationDistrict") userLocationDistrict: String = "Tanah Abang",
        @Query("userLocationProvince") userLocationProvince: String = "DKI Jakarta",
        @Query("postalCode") postalCode: String = "10230",
        @Query("firstLoad") firstLoad: Boolean = true
    ): Response<ProductSearchResponse>
}
