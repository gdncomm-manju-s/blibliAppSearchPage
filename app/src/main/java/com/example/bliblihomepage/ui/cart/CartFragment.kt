package com.example.bliblihomepage.ui.cart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentCartBinding
import com.example.bliblihomepage.util.SharedPrefManager
import com.example.bliblihomepage.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val vm: CartViewModel by activityViewModels()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var bannerAdapter: BannerAdapter

    private var page = 1
    private val pageSize = 20
    private var isLoading = false

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null

    private val MAX_DOTS = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        Log.d("DEBUG_FLOW", "CartFragment onCreateView")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("DEBUG_FLOW", "CartFragment onViewCreated - added=${isAdded}")

        // auth check
        if (!SharedPrefManager.isLoggedIn(requireContext())) {
            view.post { if (isAdded) findNavController().navigate(R.id.loginFragment) }
            return
        }

        val ctx = context ?: return
        val user = SharedPrefManager.getEmail(ctx)
        if (user.isNullOrEmpty()) {
            view.post { if (isAdded) findNavController().navigate(R.id.loginFragment) }
            return
        }

        // UI setup
        setupCartRecycler(user)
        setupBanner()
        setupClicks(user)

        // ask vm to load data
        vm.loadCart(1, pageSize, user)
        vm.loadBanner()

        observeFlows(user)
    }

    private fun setupClicks(user: String) {
        // Example searchbar click - assumes you have searchBar in binding
        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.productListFragment) // replace with actual action to search
        }

        // logout button
        binding.btnLogout.setOnClickListener {
            SharedPrefManager.logout(requireContext())
            findNavController().navigate(R.id.loginFragment)
        }

        // clear all cart
        binding.btnClearCart.setOnClickListener {
            vm.clearCart(user)
        }
    }

    private fun setupCartRecycler(user: String) {
        cartAdapter = CartAdapter(
            mutableListOf(),
            onAddClick = { },
            onDeleteClick = { p -> vm.deleteItem(p, user) }
        )

        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartAdapter

        binding.rvCart.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading) {
                    isLoading = true
                    page++
                    vm.loadCart(page, pageSize, user)
                }
            }
        })
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter(mutableListOf())
        binding.bannerPager.adapter = bannerAdapter

        binding.bannerPager.clipToPadding = false
        binding.bannerPager.clipChildren = false
        binding.bannerPager.offscreenPageLimit = 3

        // ensure visible
        binding.bannerPager.visibility = View.VISIBLE
        binding.bannerIndicator.visibility = View.VISIBLE
    }

    private fun observeFlows(user: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    vm.cartItems.collect { list ->
                        Log.d("DEBUG_FLOW", "CartFragment - cartItems size=${list.size}")
                        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        binding.tvTotalCount.text = "Total: ${list.size}"
                        cartAdapter.update(list.toMutableList())
                        isLoading = false
                    }
                }

                launch {
                    vm.banners.collect { banners ->
                        Log.d("DEBUG_FLOW", "CartFragment - banners collected size=${banners.size}")
                        if (banners.isNotEmpty()) {
                            // ensure main thread / view lifecycle
                            bannerAdapter.update(banners.toMutableList())
                            setupDots(banners.size)
                            updateSelectedDot(0)

                            // remove previous callback so we don't double-schedule
                            autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }

                            binding.bannerPager.registerOnPageChangeCallback(
                                object : ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        updateSelectedDot(position)
                                    }
                                }
                            )

                            enableAutoScroll(banners.size)

                            binding.bannerPager.visibility = View.VISIBLE
                            binding.bannerIndicator.visibility = View.VISIBLE
                        } else {
                            binding.bannerPager.visibility = View.GONE
                            binding.bannerIndicator.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // AUTO SCROLL
    private fun enableAutoScroll(size: Int) {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }

        autoScrollRunnable = object : Runnable {
            override fun run() {
                val itemCount = bannerAdapter.itemCount
                if (itemCount > 0) {
                    val next = (binding.bannerPager.currentItem + 1) % size
                    binding.bannerPager.currentItem = next
                }
                autoScrollHandler.postDelayed(this, 3000)
            }
        }
        autoScrollHandler.postDelayed(autoScrollRunnable!!, 3000)
    }

    override fun onDestroyView() {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
        binding.rvCart.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun setupDots(count: Int) {
        val dotCount = minOf(count, MAX_DOTS)

        binding.bannerIndicator.removeAllViews()

        repeat(dotCount) { index ->
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.indicator_unselected)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params

            dot.setOnClickListener {
                binding.bannerPager.currentItem = index
            }

            binding.bannerIndicator.addView(dot)
        }
    }

    private fun updateSelectedDot(position: Int) {
        val dotCount = binding.bannerIndicator.childCount
        if (dotCount == 0) return

        val normalizedPos = position % dotCount

        for (i in 0 until dotCount) {
            val dot = binding.bannerIndicator.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == normalizedPos) R.drawable.indicator_selected
                else R.drawable.indicator_unselected
            )
        }
    }
}