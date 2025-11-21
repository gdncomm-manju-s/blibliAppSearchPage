package com.example.bliblihomepage.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.data.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailBottomSheet(private val product: Product, private val onAdd: (() -> Unit)? = null) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottomsheet_product_detail, container, false)

        // find views
        val iv = v.findViewById<ImageView>(R.id.bsImg)
        val tvTitle = v.findViewById<TextView>(R.id.bsTitle)
        val tvPrice = v.findViewById<TextView>(R.id.bsPrice)
        val tvOriginal = v.findViewById<TextView>(R.id.bsOriginalPrice)
        val tvLocation = v.findViewById<TextView>(R.id.bsLocation)
        val tvDescription = v.findViewById<TextView>(R.id.bsDescription)
        val btnAdd = v.findViewById<TextView>(R.id.bsAddToCart)

        // populate safely (null checks)
        tvTitle.text = product.name ?: ""
        tvPrice.text = product.price?.priceDisplay ?: product.price?.offerPriceDisplay ?: ""
        val orig = product.price?.strikeThroughPriceDisplay ?: product.price?.listPrice?.let { formatPrice(it) } ?: ""
        if (orig.isNotBlank()) {
            tvOriginal.visibility = View.VISIBLE
            tvOriginal.text = orig
            // set strike through in code (not in xml)
            tvOriginal.paintFlags = tvOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tvOriginal.visibility = View.GONE
        }

        tvLocation.text = product.location ?: ""
        tvDescription.text = product.brand ?: ""

        // image
        val img = product.images?.firstOrNull()
        if (!img.isNullOrBlank()) {
            Glide.with(requireContext()).load(img).placeholder(R.drawable.placeholder_image).into(iv)
        } else {
            iv.setImageResource(R.drawable.placeholder_image)
        }

        btnAdd.setOnClickListener {
            onAdd?.invoke()
            dismiss()
        }

        return v
    }

    private fun formatPrice(value: Number): String {
        val nf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
        val formatted = nf.format(value)
        return formatted.replace(",00", "")
    }
}
