package com.example.bliblihomepage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentProductListBinding
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.util.AppConfig
import com.example.bliblihomepage.util.hideKeyboardFromWindow
import com.example.bliblihomepage.viewmodel.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _b: FragmentProductListBinding? = null
    private val b get() = _b!!

    private val viewModel: ProductListViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    private var searchJob: Job? = null
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentProductListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupSearch()
        setupClicks()
        setupObservers()
        viewModel.loadInitial()
    }

    private fun setupRecycler() {
        adapter = ProductAdapter(onItemClick = { openDetailBottomSheet(it) }, onAddToCart = { openDetailBottomSheet(it) })
        b.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        b.rvProducts.adapter = adapter
        b.rvProducts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && viewModel.isLoading.value == false) {
                    viewModel.loadMore()
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { list ->
            // Option A: Fullscreen "No Data Found" (replace product list)
            if (list.isEmpty()) {
                b.rvProducts.visibility = View.GONE
                b.layoutNoData.root.visibility = View.VISIBLE
            } else {
                b.layoutNoData.root.visibility = View.GONE
                b.rvProducts.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) adapter.addLoading() else adapter.removeLoading()
        }

//        // Try again button in no-data layout should reload initial results
//        b.layoutNoData.btnTryAgain.setOnClickListener {
//            b.etSearch.setText("")
//            requireContext().hideKeyboardFromWindow()
//            viewModel.loadInitial()
//        }
    }

    private fun setupSearch() {
        b.etSearch.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty().trim()

            b.ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

            searchJob?.cancel()
            searchJob = mainScope.launch {
                delay(400L) // debounce
                if (query.isEmpty()) {
                    viewModel.setSearch("")
                } else if (query.length < AppConfig.MIN_SEARCH_LENGTH) {
                    // do not search for short queries — show no-op
                    return@launch
                } else {
                    viewModel.setSearch(query)
                }
            }
        }

        b.ivClear.setOnClickListener {
            b.etSearch.setText("")
            b.ivClear.visibility = View.GONE
            requireContext().hideKeyboardFromWindow()
            viewModel.setSearch("")
        }

        b.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = b.etSearch.text.toString().trim()
                if (q.isEmpty()) viewModel.setSearch("") else viewModel.setSearch(q)
                requireContext().hideKeyboardFromWindow()
                true
            } else false
        }
    }

    private fun setupClicks() {
        b.ivBack.setOnClickListener { findNavController().navigateUp() }
        b.ivHome.setOnClickListener { findNavController().navigate(R.id.action_productList_to_homeFragment) }
    }

    private fun openDetailBottomSheet(product: Product) {
        val sheet = ProductDetailBottomSheet(product)
        sheet.show(parentFragmentManager, "detail")
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        mainScope.cancel()
        _b = null
        super.onDestroyView()
    }
}
