package com.example.bliblihomepage.repository

import android.content.Context
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.util.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val PREF = "cart_storage"
    private val sp get() = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun getProducts(user: String): List<Product> {
        val json = sp.getString("cart_$user", "[]") ?: "[]"
        return Utils.jsonToProductList(json)
    }

    fun saveProducts(user: String, list: List<Product>) {
        val json = Utils.productListToJson(list)
        sp.edit().putString("cart_$user", json).apply()
    }

    suspend fun addProduct(user: String, product: Product): Boolean = withContext(Dispatchers.IO) {
        val list = getProducts(user).toMutableList()

        val pid = getProductId(product)
        if (list.none { getProductId(it) == pid }) {
            list.add(product)
            saveProducts(user, list)
            return@withContext true
        }
        false
    }

    suspend fun deleteProduct(user: String, product: Product) = withContext(Dispatchers.IO) {
        val pid = getProductId(product)
        val list = getProducts(user).toMutableList()
        list.removeAll { getProductId(it) == pid }
        saveProducts(user, list)
    }

    suspend fun clearCart(user: String) = withContext(Dispatchers.IO) {
        saveProducts(user, emptyList())
    }

    /** Extract correct product ID (handles id, sku, itemId, etc.) */
    private fun getProductId(p: Product): String {
        return when {
            !p.id.isNullOrEmpty() -> p.id!!
            !p.sku.isNullOrEmpty() -> p.sku!!
            else -> p.hashCode().toString()
        }
    }
}

