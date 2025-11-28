//package com.example.bliblihomepage.ui.productListPage
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.bliblihomepage.databinding.FragmentSearchBinding
//import com.example.bliblihomepage.repository.SearchRepository
//import com.example.bliblihomepage.util.Constants
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//class SearchFragment : Fragment() {
//    private var _binding: FragmentSearchBinding? = null
//    private val binding get() = _binding!!
//    private val repo = SearchRepository() // implement network calls here
//    private lateinit var adapter: SearchAdapter
//    private var searchJob: Job? = null
//    private var currentQuery = ""
//    private var currentPage = 0
//    private var endReached = false
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
//        FragmentSearchBinding.inflate(inflater, container, false).also { _binding = it }.root
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        adapter = SearchAdapter(mutableListOf()) { productJson ->
//            // show bottom sheet similar to CartFragment
//        }
//        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvSearch.adapter = adapter
//
//        binding.etSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun afterTextChanged(s: Editable?) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                searchJob?.cancel()
//                searchJob = lifecycleScope.launch {
//                    delay(Constants.SEARCH_DEBOUNCE_MS)
//                    val query = s.toString().trim()
//                    if (query.length >= Constants.MIN_SEARCH_CHARS) {
//                        currentQuery = query
//                        currentPage = 0
//                        endReached = false
//                        performSearch(query, currentPage)
//                    } else {
//                        // show empty screen or placeholder
//                        adapter.submitList(emptyList())
//                    }
//                }
//            }
//        })
//
//        binding.rvSearch.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
//            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
//                val lm = rv.layoutManager as LinearLayoutManager
//                val total = lm.itemCount
//                val last = lm.findLastVisibleItemPosition()
//                if (last >= total - 2 && !endReached) {
//                    performSearch(currentQuery, currentPage + 1)
//                }
//            }
//        })
//    }
//
//    private fun performSearch(query: String, page: Int) {
//        lifecycleScope.launch {
//            binding.searchProgress.visibility = View.VISIBLE
//            val res = repo.searchPage(query, page, /* pageSize = */ 20)
//            binding.searchProgress.visibility = View.GONE
//            if (res.isSuccess) {
//                val list = res.getOrNull() ?: emptyList()
//                if (page == 0) adapter.submitList(list) else adapter.addPage(list)
//                if (list.isNotEmpty()) currentPage = page
//                if (list.size < 20) endReached = true
//            } else {
//                // show error
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
