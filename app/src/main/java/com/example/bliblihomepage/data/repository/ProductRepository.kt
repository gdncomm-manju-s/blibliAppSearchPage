package com.example.bliblihomepage.data.repository

import android.content.Context
import com.example.bliblihomepage.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val context: Context) {

    // load all products from assets/products.json
    fun loadAllProducts(): List<Product> {
        return try {
            val json = context.assets.open("productList.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson<List<Product>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}


