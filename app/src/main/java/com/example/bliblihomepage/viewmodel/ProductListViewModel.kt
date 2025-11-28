package com.example.bliblihomepage.viewmodel

import androidx.lifecycle.*
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.repository.ProductDataRepository
import com.example.bliblihomepage.util.AppConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repo: ProductDataRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentQuery = ""
    private var page = 1
    private var loadedList = mutableListOf<Product>()
    private var job: Job? = null

    var hasSearchStarted = false
        private set

    // When search bar empty → show BLANK
    fun initBlank() {
        hasSearchStarted = false
        loadedList.clear()
        currentQuery = ""
        page = 1
        _products.value = emptyList()
    }

    fun startSearch() {
        hasSearchStarted = true
    }

    fun search(q: String) {
        if (q.length < AppConfig.MIN_SEARCH_LENGTH) {
            _products.value = emptyList()
            return
        }

        currentQuery = q
        page = 1
        loadedList.clear()

        job?.cancel()
        job = viewModelScope.launch {
            delay(200) // Debounce

            _isLoading.value = true

            val result = repo.searchProducts(q, page, 0)
            val list = result.getOrNull().orEmpty()

            loadedList.addAll(list)
            _products.value = loadedList

            _isLoading.value = false
        }
    }

    fun loadMore() {
        if (_isLoading.value == true || currentQuery.isBlank()) return

        page++

        viewModelScope.launch {
            _isLoading.value = true

            val result = repo.searchProducts(
                currentQuery,
                page,
                (page - 1) * AppConfig.PAGE_SIZE
            )

            val next = result.getOrNull().orEmpty()

            if (next.isNotEmpty()) {
                loadedList.addAll(next)
                _products.value = loadedList
            }

            _isLoading.value = false
        }
    }
}
