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
import com.example.bliblihomepage.util.AppConfig
import com.example.bliblihomepage.util.loadUrlSafe
import java.util.Locale

class CartAdapter(
    private val items: MutableList<Product> = mutableListOf(),
    private val onAddClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartVH>() {

    inner class CartVH(val b: ItemProductCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartVH(binding)
    }

    override fun onBindViewHolder(holder: CartVH, position: Int) {
        val p = items[position]
        val b = holder.b

        // IMAGE
        Glide.with(holder.itemView)
            .load(p.images.firstOrNull())
            .placeholder(R.drawable.placeholder_image)
            .into(b.imgProduct)

        // TITLE
        b.tvTitle.text = p.name ?: ""

        // PRICE
        b.tvPrice.text = p.price.priceDisplay ?: ""

        // ORIGINAL PRICE (strike-through) — using strikeThroughPriceDisplay field
        if (!p.price.strikeThroughPriceDisplay.isNullOrEmpty()) {
            b.tvOriginalPrice.apply {
                text = p.price.strikeThroughPriceDisplay
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                visibility = View.VISIBLE
            }
        } else b.tvOriginalPrice.visibility = View.GONE

        // DISCOUNT (from Price.discount if >0)
        if (p.price.discount > 0) {
            b.tvDiscount.apply {
                text = "${p.price.discount}%"
                visibility = View.VISIBLE
            }
        } else b.tvDiscount.visibility = View.GONE

        // rating row
        val rating = p.review?.absoluteRating ?: 0.0
        val sold = p.soldCountTotal.takeIf { it > 0 } ?: p.review?.count ?: 0
        if (rating > 0.0) {
            b.ratingRow.visibility = View.VISIBLE
            b.tvRating.text = String.Companion.format(Locale.getDefault(), "%.1f", rating)
            b.tvSoldSmall.text = "Terjual $sold"
        } else {
            b.ratingRow.visibility = View.GONE
        }

        // location
        b.tvLocation.text = p.location ?: ""
        b.tvLocation.visibility = if (!p.location.isNullOrBlank()) View.VISIBLE else View.GONE

        // tags
        val tags = p.tags.map { it.uppercase(Locale.getDefault()) }
        b.ivBadgeFreeShip.visibility = if ("FREE_SHIPPING" in tags) View.VISIBLE else View.GONE
        b.ivBadgeCnc.visibility =
            if ("CNC_AVAILABLE" in tags || "CLICK_COLLECT" in tags) View.VISIBLE else View.GONE

        // image with placeholder + error
        val img = p.images.firstOrNull()
        if (!img.isNullOrBlank()) {
            b.imgProduct.loadUrlSafe(img)
        } else {
            b.imgProduct.setImageResource(AppConfig.PLACEHOLDER_RES)
        }
        // LOCATION
        b.tvLocation.text = p.location ?: ""

        // MERCHANT BADGES
        if (!p.badge?.merchantBadgeUrl.isNullOrEmpty()) {
            b.imgMerchantBadge.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(p.badge!!.merchantBadgeUrl)
                .into(b.imgMerchantBadge)
        } else b.imgMerchantBadge.visibility = View.GONE

        // OFFICIAL STORE BADGE
        b.imgOfficial.visibility = if (!p.brand.isNullOrBlank()) View.VISIBLE else View.GONE

        // Buttons: show delete
        b.btnDelete.visibility = View.VISIBLE
        b.btnDelete.setOnClickListener { onDeleteClick(p) }

    }

    override fun getItemCount() = items.size

    fun update(newList: List<Product>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
