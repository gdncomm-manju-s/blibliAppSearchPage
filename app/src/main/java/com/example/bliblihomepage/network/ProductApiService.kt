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
        @Query("start") start: Int = 0
    ): Response<ProductSearchResponse>
}
