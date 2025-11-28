package com.example.bliblihomepage.util

import com.example.bliblihomepage.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object Utils {
    private val gson = Gson()

    fun productToJsonString(product: Product): String {
        return gson.toJson(product)
    }

    fun jsonToProduct(json: String): Product? {
        return try {
            gson.fromJson(json, Product::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // ---- new helpers ----
    fun productListToJson(list: List<Product>): String {
        return gson.toJson(list)
    }

    fun jsonToProductList(json: String): List<Product> {
        return try {
            val type = object : TypeToken<List<Product>>() {}.type
            gson.fromJson<List<Product>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

