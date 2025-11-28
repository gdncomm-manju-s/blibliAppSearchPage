package com.example.bliblihomepage.ui.productListPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentProductListBinding
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.util.AppConfig
import com.example.bliblihomepage.util.SharedPrefManager
import com.example.bliblihomepage.viewmodel.CartViewModel
import com.example.bliblihomepage.viewmodel.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductListViewModel by viewModels()
    private val cartVm: CartViewModel by activityViewModels()

    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecycler()
        setupSearch()
        setupObservers()
    }

    private fun setupRecycler() {
        adapter = ProductAdapter(
            onItemClick = { openDetailBottomSheet(it) },
            onAddToCart = { openDetailBottomSheet(it) }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1)) viewModel.loadMore()
            }
        })
    }

    private fun setupObservers() {

        viewModel.products.observe(viewLifecycleOwner) { list ->

            // 1️⃣ Before any search → BLANK SCREEN
            if (!viewModel.hasSearchStarted) {
                binding.rvProducts.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.GONE
                return@observe
            }

            // 2️⃣ After search started → show results OR empty layout
            if (list.isEmpty()) {
                binding.rvProducts.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            } else {
                binding.layoutNoData.root.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            binding.ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

            // When empty → blank screen
            if (query.isEmpty()) {
                viewModel.initBlank()
                return@addTextChangedListener
            }

            // Trigger search only if ≥ MIN chars
            if (query.length >= AppConfig.MIN_SEARCH_LENGTH) {
                viewModel.startSearch()
                viewModel.search(query)
            }
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
            viewModel.initBlank()
        }

        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
        binding.ivCart.setOnClickListener { findNavController().navigateUp() }

        binding.ivHome.setOnClickListener { findNavController().navigate(R.id.action_productList_to_homeFragment) }

    }


    private fun openDetailBottomSheet(product: Product) {
        val user = SharedPrefManager.getEmail(requireContext()) ?: return

        val sheet = ProductDetailBottomSheet(product) {
            cartVm.addToCart(product, user) { added ->
                if (added) findNavController().navigate(R.id.cartFragment)
            }
        }
        sheet.show(parentFragmentManager, "detail")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
