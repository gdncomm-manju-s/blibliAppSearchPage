package com.example.bliblihomepage.repository

import com.example.bliblihomepage.network.ProductApiService
import com.example.bliblihomepage.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.example.bliblihomepage.model.Product


class ProductDataRepository @Inject constructor(
    private val api: ProductApiService
) {

    /**
     * Search products using your real API.
     * @param searchTerm - keyword user types
     * @param page - page number (starts from 1)
     * @param start - offset for backend (start = (page-1)*pageSize)
     */
    suspend fun searchProducts(
        searchTerm: String,
        page: Int = 1,
        start: Int = 0
    ): Result<List<Product>> = withContext(Dispatchers.IO) {

        return@withContext try {
            val response = api.searchProducts(
                searchTerm = searchTerm,
                page = page,
                start = start
            )

            if (response.isSuccessful) {
                val body = response.body()
                val productList = body?.data?.products ?: emptyList()

                Result.success(productList)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

