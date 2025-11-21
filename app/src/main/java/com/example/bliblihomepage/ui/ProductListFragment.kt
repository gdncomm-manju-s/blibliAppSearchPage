package com.example.bliblihomepage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bliblihomepage.R
import com.example.bliblihomepage.data.model.Product
import com.example.bliblihomepage.databinding.FragmentProductListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.InputStreamReader

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _b: FragmentProductListBinding? = null
    private val b get() = _b!!

    private lateinit var adapter: ProductAdapter

    private var allProducts = listOf<Product>()
    private var currentQuery: String? = null

    private val pageSize = 40
    private var currentPage = 0
    private var isLoading = false

    private var loadingJob: Job? = null
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentProductListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecycler()
        setupSearch()
        setupClicks()
        loadProductsFromJson()
    }

    // RecyclerView setup
    private fun setupRecycler() {
        adapter = ProductAdapter(
            onItemClick = { openDetailBottomSheet(it) },
            onAddToCart = { openDetailBottomSheet(it) }
        )
        b.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        b.rvProducts.adapter = adapter

        b.rvProducts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading) {
                    loadNextPage()
                }
            }
        })
    }

    // Search + debounce + reset
    private fun setupSearch() {
        b.etSearch.addTextChangedListener(afterTextChanged = { editable ->
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(300)

                currentQuery = editable?.toString()
                b.ivClear.visibility =
                    if (currentQuery.isNullOrEmpty()) View.GONE else View.VISIBLE

                resetPagination()
                loadNextPage()
            }
        })

        b.ivClear.setOnClickListener {
            b.etSearch.setText("")
            currentQuery = null
            b.ivClear.visibility = View.GONE

            resetPagination()
            loadNextPage()
        }

        b.etSearch.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                currentQuery = b.etSearch.text.toString()
                resetPagination()
                loadNextPage()
                true
            } else false
        }
    }

    // Home + Back buttons
    private fun setupClicks() {
        b.ivBack.setOnClickListener { findNavController().navigateUp() }

        b.ivHome.setOnClickListener {
            findNavController().navigate(R.id.action_productList_to_homeFragment)
        }
    }

    // Load products from /assets/productList.json
    private fun loadProductsFromJson() {
        try {
            val input = requireContext().assets.open("productList.json")
            val reader = InputStreamReader(input)
            val type = object : TypeToken<List<Product>>() {}.type
            allProducts = Gson().fromJson(reader, type)
            reader.close()
        } catch (e: Exception) {
            allProducts = emptyList()
        }

        resetPagination()
        loadNextPage()
    }

    // Pagination handling
    private fun resetPagination() {
        currentPage = 0
        adapter.setData(emptyList())
    }

    private fun loadNextPage() {
        if (isLoading) return
        isLoading = true
        adapter.addLoading()

        loadingJob?.cancel()
        loadingJob = CoroutineScope(Dispatchers.Main).launch {

            delay(250) // small delay for smoothness

            val source = if (currentQuery.isNullOrBlank()) {
                allProducts
            } else {
                allProducts.filter {
                    it.name.contains(currentQuery!!, ignoreCase = true)
                }
            }

            if (source.isEmpty()) {
                adapter.removeLoading()
                b.layoutNoData.tvNoData.visibility = View.VISIBLE
                isLoading = false
                return@launch
            } else {
                b.layoutNoData.tvNoData.visibility = View.GONE
            }

            val from = currentPage * pageSize
            val to = minOf(from + pageSize, source.size)

            if (from >= source.size) {
                adapter.removeLoading()
                isLoading = false
                return@launch
            }

            val page = source.subList(from, to)

            adapter.removeLoading()

            if (currentPage == 0) {
                adapter.setData(page)
            } else {
                adapter.appendData(page)
            }

            currentPage++
            isLoading = false
        }
    }

    // Detail BottomSheet
    private fun openDetailBottomSheet(product: Product) {
        val sheet = ProductDetailBottomSheet(product)
        sheet.show(parentFragmentManager, "detail")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingJob?.cancel()
        searchJob?.cancel()
        _b = null
    }
}
