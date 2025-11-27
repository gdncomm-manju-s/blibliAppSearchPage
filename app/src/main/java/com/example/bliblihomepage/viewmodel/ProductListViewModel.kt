package com.example.bliblihomepage.viewmodel

import androidx.lifecycle.*
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.repository.ProductDataRepository
import com.example.bliblihomepage.util.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

//Fetching data from the repository
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repo: ProductDataRepository
) : ViewModel() {

    //Fragment observes this to update UI
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    //Stores ALL products fetched from API
    private var allProducts: List<Product> = emptyList()

    //Stores search filtered products
    private var filteredProducts: List<Product> = emptyList()

    private var page = 0
    private var currentQuery: String = ""
    private var fetchJob: Job? = null

    // initial load
    fun loadInitial() {
        if (_products.value?.isNotEmpty() == true) return
        fetchAllAndReset(AppConfig.DEFAULT_SEARCH_TERM)
    }

    // fetch from API and reset paging
    private fun fetchAllAndReset(searchTerm: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            allProducts = repo.fetchProducts(searchTerm)
            filteredProducts = allProducts
            page = 0
            loadPage(reset = true)
            _isLoading.value = false
        }
    }

    // search called from UI
    fun setSearch(query: String) {
        val trimmed = query.trim()
        currentQuery = trimmed

        viewModelScope.launch {
            page = 0
            filteredProducts = if (trimmed.isBlank()) {
                allProducts
            } else {
                allProducts.filter { it.name?.contains(trimmed, ignoreCase = true) == true }
            }
            loadPage(reset = true)
        }
    }

    fun loadMore() {
        if (_isLoading.value == true) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(AppConfig.LOAD_DELAY_MS)
            loadPage(reset = false)
            _isLoading.value = false
        }
    }

    private fun loadPage(reset: Boolean) {
        val start = page * AppConfig.PAGE_SIZE
        if (start >= filteredProducts.size) {
            if (reset) _products.value = emptyList()
            return
        }

        val end = minOf(start + AppConfig.PAGE_SIZE, filteredProducts.size)
        val pageData = filteredProducts.subList(start, end)

        if (reset) {
            _products.value = pageData
        } else {
            val current = _products.value?.toMutableList() ?: mutableListOf()
            current.addAll(pageData)
            _products.value = current
        }
        page++
    }
}
