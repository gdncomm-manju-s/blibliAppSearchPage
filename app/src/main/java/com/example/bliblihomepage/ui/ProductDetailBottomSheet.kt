package com.example.bliblihomepage.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.BottomsheetProductDetailBinding
import com.example.bliblihomepage.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailBottomSheet(private val product: Product, private val onAdd: (() -> Unit)? = null) :
    BottomSheetDialogFragment() {

    private var _b: BottomsheetProductDetailBinding? = null
    private val b get() = _b!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = BottomsheetProductDetailBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.bsTitle.text = product.name ?: ""
        b.bsPrice.text = product.price.priceDisplay ?: product.price.offerPriceDisplay ?: ""
        val orig = product.price.strikeThroughPriceDisplay ?: product.price.listPrice?.let { formatPrice(it) } ?: ""
        if (orig.isNotBlank()) {
            b.bsOriginalPrice.visibility = View.VISIBLE
            b.bsOriginalPrice.text = orig
            b.bsOriginalPrice.paintFlags = b.bsOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else b.bsOriginalPrice.visibility = View.GONE

        b.bsLocation.text = product.location ?: ""
        b.bsDescription.text = product.brand ?: ""

        val img = product.images.firstOrNull()
        if (!img.isNullOrBlank()) {
            Glide.with(requireContext()).load(img).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(b.bsImg)
        } else b.bsImg.setImageResource(R.drawable.placeholder_image)

        b.bsAddToCart.setOnClickListener {
            onAdd?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    private fun formatPrice(value: Number): String {
        val nf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
        return nf.format(value).replace(",00", "")
    }
}
