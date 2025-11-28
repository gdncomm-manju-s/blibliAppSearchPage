package com.example.bliblihomepage.ui.productListPage

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
import androidx.recyclerview.widget.RecyclerView
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

    private var _listBinding: FragmentProductListBinding? = null
    private val binding get() = _listBinding!!

    private val viewModel: ProductListViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    private var searchJob: Job? = null
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _listBinding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupSearch()
        setupClicks()
        setupObservers()
        viewModel.loadInitial()
    }

    //Initializes RecyclerView
    private fun setupRecycler() {
        adapter = ProductAdapter(
            onItemClick = { openDetailBottomSheet(it) },
            onAddToCart = { openDetailBottomSheet(it) })
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter
        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && viewModel.isLoading.value == false) {
                    viewModel.loadMore()
                }
            }
        })
    }

    //Observes LiveData from ViewModel
    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { list ->

            //No Data Found view
            if (list.isEmpty()) {
                binding.rvProducts.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            }
            //update adapter
            else {
                binding.layoutNoData.root.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) adapter.addLoading() else adapter.removeLoading()
        }

        // Try again button in no-data layout should reload initial results
        binding.layoutNoData.btnTryAgain.setOnClickListener {
            binding.etSearch.setText("")
            requireContext().hideKeyboardFromWindow()
            viewModel.loadInitial()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty().trim()

            binding.ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

                //Search happens after 400ms user stops typing

                if (query.isEmpty()) {
                    viewModel.setSearch("")
                } else if (query.length < AppConfig.MIN_SEARCH_LENGTH) {

                } else {
                    viewModel.setSearch(query)
                }
            }

        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
            binding.ivClear.visibility = View.GONE
            requireContext().hideKeyboardFromWindow()
            viewModel.setSearch("")
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = binding.etSearch.text.toString().trim()
                if (q.isEmpty()) viewModel.setSearch("") else viewModel.setSearch(q)
                requireContext().hideKeyboardFromWindow()
                true
            } else false
        }
    }

    private fun setupClicks() {
        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
        binding.ivHome.setOnClickListener { findNavController().navigate(R.id.action_productList_to_homeFragment) }
    }

    private fun openDetailBottomSheet(product: Product) {
        val sheet = ProductDetailBottomSheet(product)
        sheet.show(parentFragmentManager, "detail")
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        mainScope.cancel()
        _listBinding = null
        super.onDestroyView()
    }
}