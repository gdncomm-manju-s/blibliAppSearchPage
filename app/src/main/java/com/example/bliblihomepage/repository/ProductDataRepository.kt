package com.example.bliblihomepage.repository

import android.content.Context
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.model.ProductSearchResponse
import com.example.bliblihomepage.network.ProductApiService
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDataRepository @Inject constructor(
    private val api: ProductApiService,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {

    //responsible for fetching product data from the API
    suspend fun fetchProducts(searchTerm: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val resp: Response<ProductSearchResponse> = api.searchProducts(searchTerm)
            if (resp.isSuccessful) {
                val products = resp.body()?.data?.products ?: emptyList()
                return@withContext validateProducts(products)
            }
        } catch (t: Throwable) {

        }

        return@withContext loadFromAssets("productList.json")
    }

    private fun validateProducts(list: List<Product>): List<Product> {
        return list.filter { p ->
            val hasName = !p.name.isNullOrBlank()
            val hasPrice = !p.price.priceDisplay.isNullOrBlank()
                    || p.price.salePrice != null
                    || p.price.listPrice != null
            hasName && hasPrice
        }
    }

    private fun loadFromAssets(fileName: String): List<Product> {
        return try {
            val input = context.assets.open(fileName)
            val json = input.bufferedReader().use { it.readText() }
            val type = com.google.gson.reflect.TypeToken.getParameterized(
                List::class.java,
                Product::class.java
            ).type
            val items: List<Product> = gson.fromJson(json, type)
            validateProducts(items)
        } catch (t: Throwable) {
            emptyList()
        }
    }
}
