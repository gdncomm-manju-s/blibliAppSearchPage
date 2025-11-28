package com.example.bliblihomepage.ui.productListPage

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.ItemProductCardBinding
import com.example.bliblihomepage.model.Product

class SearchAdapter(
    private val items: MutableList<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchVH>() {

    inner class SearchVH(val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVH {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchVH(binding)
    }

    override fun onBindViewHolder(holder: SearchVH, position: Int) {
        val item = items[position]
        val b = holder.binding

        // ----- PRODUCT IMAGE -----
        Glide.with(holder.itemView)
            .load(item.images.firstOrNull())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(b.imgProduct)

        // ----- TITLE -----
        b.tvTitle.text = item.name

        // ----- PRICE -----
        b.tvPrice.text = item.price.priceDisplay ?: ""

        // ----- ORIGINAL PRICE -----
        if (!item.price.strikeThroughPriceDisplay.isNullOrEmpty()) {
            b.tvOriginalPrice.text = item.price.strikeThroughPriceDisplay
            b.tvOriginalPrice.paintFlags =
                b.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            b.tvOriginalPrice.text = ""
            b.tvOriginalPrice.paintFlags = 0
        }

        // ----- DISCOUNT -----
        if (item.price.discount > 0) {
            b.tvDiscount.visibility = View.VISIBLE
            b.tvDiscount.text = "${item.price.discount}%"
        } else {
            b.tvDiscount.visibility = View.GONE
        }

        // ----- RATING & SOLD -----
        b.tvRating.text = (item.review?.absoluteRating ?: 0.0).toString()
        b.tvSoldSmall.text = "${item.soldCountTotal}+ sold"

        // ----- FREE SHIPPING BADGE -----
        b.ivBadgeFreeShip.visibility =
            if (item.tags.contains("FREE_SHIPPING")) View.VISIBLE else View.GONE

        // ----- CNC BADGE -----
        b.ivBadgeCnc.visibility =
            if (item.tags.contains("CNC")) View.VISIBLE else View.GONE

        // ----- MERCHANT BADGE IMAGE -----
        val merchantBadge = item.badge?.merchantBadgeUrl
        if (!merchantBadge.isNullOrEmpty()) {
            b.imgMerchantBadge.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(merchantBadge)
                .into(b.imgMerchantBadge)
        } else {
            b.imgMerchantBadge.visibility = View.GONE
        }

        // ----- OFFICIAL STORE LOGIC -----
        val isOfficial = item.brand != null && item.brand.lowercase() != "no brand"
        b.imgOfficial.visibility = if (isOfficial) View.VISIBLE else View.GONE

        // ----- LOCATION -----
        b.tvLocation.text = item.location ?: ""

        // ----- ADD TO CART -----
//        b.btnAddToCart.setOnClickListener { onAddClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun addMore(list: List<Product>) {
        val start = items.size
        items.addAll(list)
        notifyItemRangeInserted(start, list.size)
    }
}
