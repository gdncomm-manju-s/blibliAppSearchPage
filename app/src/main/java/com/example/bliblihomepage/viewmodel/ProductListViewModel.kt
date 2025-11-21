package com.example.bliblihomepage.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.bliblihomepage.data.repository.ProductRepository
import com.example.bliblihomepage.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {

    private val repo = ProductRepository(app.applicationContext)

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isGrid = MutableLiveData(false)
    val isGrid: LiveData<Boolean> = _isGrid

    private var allProducts: List<Product> = emptyList()

    private var page = 0
    private val pageSize = 50

    // ACTIVE SEARCH STRING
    private var currentQuery: String? = null

    init {
        loadInitial()
    }

    fun loadInitial() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            allProducts = repo.loadAllProducts()

            page = 0
            val first = getPage(allProducts)

            _products.postValue(first)
            _isLoading.postValue(false)
        }
    }

    // PAGINATION
    private fun getPage(source: List<Product>): List<Product> {
        val start = page * pageSize
        if (start >= source.size) return _products.value ?: emptyList()

        val end = kotlin.math.min(start + pageSize, source.size)

        val current = (_products.value ?: emptyList()).toMutableList()
        current.addAll(source.subList(start, end))

        page++
        return current
    }

    fun loadMore() {
        if (_isLoading.value == true) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            // active list depends on search
            val src = if (!currentQuery.isNullOrBlank()) {
                allProducts.filter { it.name.contains(currentQuery!!, ignoreCase = true) }
            } else {
                allProducts
            }

            Thread.sleep(250) // simulate small delay

            val next = getPage(src)
            _products.postValue(next)
            _isLoading.postValue(false)
        }
    }

    // TOGGLE LIST / GRID
    fun toggleView() {
        _isGrid.value = !(_isGrid.value ?: false)
    }

    // SEARCH SYSTEM
    fun setSearch(q: String?) {
        currentQuery = q

        viewModelScope.launch(Dispatchers.Default) {

            // When search is empty → FULL RESET
            if (q.isNullOrBlank()) {
                page = 0
                val first = getPage(allProducts)
                _products.postValue(first)
                return@launch
            }

            // Filter source
            val filtered = allProducts.filter {
                it.name.contains(q, ignoreCase = true)
            }

            page = 0
            val first = getPage(filtered)
            _products.postValue(first)
        }
    }
}
