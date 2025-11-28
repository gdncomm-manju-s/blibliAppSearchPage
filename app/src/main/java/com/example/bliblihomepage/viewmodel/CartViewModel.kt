package com.example.bliblihomepage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.repository.BannerRepository
import com.example.bliblihomepage.repository.CartRepository
import com.example.bliblihomepage.util.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repo: CartRepository,
    private val bannerRepo: BannerRepository
) : ViewModel() {

    private val _banners = MutableStateFlow<List<String>>(emptyList())
    val banners: StateFlow<List<String>> = _banners


    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems = _cartItems

    fun loadCart(page: Int, size: Int, user: String) {
        viewModelScope.launch {
            val list = repo.getProducts(user)
            _cartItems.value = list.take(page * size)
        }
    }

    fun addToCart(p: Product, user: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.addProduct(user, p)
            callback(result)
            loadCart(1, 50, user)
        }
    }

    fun deleteItem(p: Product, user: String) {
        viewModelScope.launch {
            repo.deleteProduct(user, p)
            loadCart(1, 50, user)
        }
    }

    fun clearCart(user: String) {
        viewModelScope.launch {
            repo.clearCart(user)
            loadCart(1, 50, user)
        }
    }

    fun loadBanner() {
        viewModelScope.launch {
            val b = bannerRepo.getBannerImages()
            _banners.value = b
        }
    }
}

