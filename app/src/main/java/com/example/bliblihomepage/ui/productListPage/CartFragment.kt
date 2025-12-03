package com.example.bliblihomepage.ui.productListPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.FragmentCartBinding
import com.example.bliblihomepage.util.SharedPrefManager
import com.example.bliblihomepage.viewmodel.CartViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val vm: CartViewModel by activityViewModels()

    // keep adapter instance so we don't reset it repeatedly
    private lateinit var cartAdapter: CartAdapter
    private lateinit var bannerAdapter: BannerAdapter

    private var page = 1
    private val pageSize = 50
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = SharedPrefManager.getEmail(requireContext())

        // If no user, go to login
        if (user.isNullOrEmpty()) {
            findNavController().navigate(R.id.loginFragment)
            return
        }

        // Search navigate
        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.productListFragment)
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            SharedPrefManager.logout(requireContext())
            findNavController().navigate(R.id.loginFragment)
        }

        // Clear entire cart
        binding.btnClearCart.setOnClickListener {
            vm.clearCart(user)
        }

        // Setup RecyclerView and adapter once
        cartAdapter = CartAdapter(
            mutableListOf(),
            onAddClick = { /* not used in cart */ },
            onDeleteClick = { p ->
                vm.deleteItem(p, user)
            })
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartAdapter

        // Pagination
        binding.rvCart.addOnScrollListener(object :
            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                rv: androidx.recyclerview.widget.RecyclerView,
                dx: Int,
                dy: Int
            ) {
                if (!rv.canScrollVertically(1) && !isLoading) {
                    isLoading = true
                    page++
                    vm.loadCart(page, pageSize, user)
                }
            }
        })

        bannerAdapter = BannerAdapter(mutableListOf())
        binding.bannerPager.adapter = bannerAdapter


        // Load data
        vm.loadCart(1, pageSize, user)
        vm.loadBanner()

        // Observe flows lifecycle-safely
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.cartItems.collect { list ->
                        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        binding.tvTotalCount.text = "Total: ${list.size}"

                        cartAdapter.update(list.toMutableList())
                        isLoading = false
                    }
                }

                launch {
                    vm.banners.collect { banners ->
                        if (banners.isNotEmpty()) {

                            bannerAdapter.update(banners)

                            // Create dots (max 5)
                            setupDots(banners.size)

                            // Update selected dot when pager scrolls
                            binding.bannerPager.registerOnPageChangeCallback(
                                object : ViewPager2.OnPageChangeCallback() {
                                    override fun onPageSelected(position: Int) {
                                        updateSelectedDot(position)
                                    }
                                }
                            )

                            // Mark first dot as selected
                            updateSelectedDot(0)

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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

    }

    private val MAX_DOTS = 5

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
