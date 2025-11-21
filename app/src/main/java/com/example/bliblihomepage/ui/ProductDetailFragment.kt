package com.example.bliblihomepage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bliblihomepage.databinding.FragmentProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _b: FragmentProductDetailBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProductDetailBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val name = arguments?.getString("productName") ?: ""
        val image = arguments?.getString("productImage")
        val price = arguments?.getString("productPrice") ?: ""

        // Set UI
        b.tvTitle.text = name
        b.tvPrice.text = price

        if (!image.isNullOrBlank()) {
            Glide.with(requireContext())
                .load(image)
                .into(b.imgMain)
        }

        // Add to Bag (with bottom sheet)
        b.btnAddToBag.setOnClickListener {
            AddToBagBottomSheet(name) {
                b.btnAddToBag.apply {
                    text = "Added to Bag"
                    isEnabled = false
                }
            }.show(parentFragmentManager, "AddToBagBottomSheet")
        }

        b.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
