package com.example.bliblihomepage.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.ItemLoadingBinding
import com.example.bliblihomepage.databinding.ItemProductListBinding
import com.example.bliblihomepage.model.Product
import com.example.bliblihomepage.util.AppConfig
import com.example.bliblihomepage.util.loadUrlSafe
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Product>()
    private var showLoading = false

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_LOADING = 1
    }

    override fun getItemCount(): Int = items.size + if (showLoading) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (showLoading && position == itemCount - 1) TYPE_LOADING else TYPE_LIST
    }

    fun setData(list: List<Product>) {
        items.clear()
        items.addAll(filterValid(list))
        notifyDataSetChanged()
    }

    fun appendData(list: List<Product>) {
        val filtered = filterValid(list)
        val start = items.size
        items.addAll(filtered)
        notifyItemRangeInserted(start, filtered.size)
    }

    fun addLoading() {
        if (!showLoading) {
            showLoading = true
            notifyItemInserted(itemCount - 1)
        }
    }

    fun removeLoading() {
        if (showLoading) {
            val pos = itemCount - 1
            showLoading = false
            notifyItemRemoved(pos)
        }
    }

    private fun filterValid(list: List<Product>): List<Product> {
        return list.filter { p ->
            val hasName = !p.name.isNullOrBlank()
            val hasPrice = !p.price.priceDisplay.isNullOrBlank() || p.price.salePrice != null || p.price.listPrice != null
            hasName && hasPrice
        }
    }

    // ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LIST -> ListVH(ItemProductListBinding.inflate(inflater, parent, false))
            else -> LoadingVH(ItemLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingVH) return
        if (position >= items.size) return
        (holder as ListVH).bind(items[position])
    }

    inner class ListVH(private val b: ItemProductListBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(p: Product) {
            b.tvTitle.text = p.name

            // price display with fallback formatting
            val priceVal = p.price.salePrice ?: p.price.listPrice ?: p.price.minPrice ?: 0
            b.tvPrice.text = p.price.priceDisplay ?: formatPrice(priceVal)

            // original price (strike)
            val orig = p.price.strikeThroughPriceDisplay
            if (!orig.isNullOrEmpty()) {
                b.tvOriginalPrice.visibility = View.VISIBLE
                b.tvOriginalPrice.text = orig
                b.tvOriginalPrice.paintFlags = b.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                b.tvOriginalPrice.visibility = View.GONE
            }

            // discount
            val disc = p.price.discount
            b.tvDiscount.visibility = if (disc > 0) View.VISIBLE else View.GONE
            if (disc > 0) b.tvDiscount.text = "$disc%"

            // rating row
            val rating = p.review?.absoluteRating ?: 0.0
            val sold = p.soldCountTotal.takeIf { it > 0 } ?: p.review?.count ?: 0
            if (rating > 0.0) {
                b.ratingRow.visibility = View.VISIBLE
                b.tvRating.text = String.format(Locale.getDefault(), "%.1f", rating)
                b.tvSoldSmall.text = "Terjual $sold"
            } else {
                b.ratingRow.visibility = View.GONE
            }

            // location
            b.tvLocation.text = p.location ?: ""
            b.tvLocation.visibility = if (!p.location.isNullOrBlank()) View.VISIBLE else View.GONE

            // badges
            applyBadges(p, b.imgOfficial, b.imgMerchantBadge)

            // tags
            val tags = p.tags.map { it.uppercase(Locale.getDefault()) }
            b.ivBadgeFreeShip.visibility = if ("FREE_SHIPPING" in tags) View.VISIBLE else View.GONE
            b.ivBadgeCnc.visibility = if ("CNC_AVAILABLE" in tags || "CLICK_COLLECT" in tags) View.VISIBLE else View.GONE

            // image with placeholder + error
            val img = p.images.firstOrNull()
            if (!img.isNullOrBlank()) {
                b.imgProduct.loadUrlSafe(img)
            } else {
                b.imgProduct.setImageResource(AppConfig.PLACEHOLDER_RES)
            }

            // ensure image and button aligned bottom using existing xml constraints
            // (Make sure your item_product_list.xml uses ConstraintLayout with img and button anchored to bottom)

            b.root.setOnClickListener { onItemClick(p) }
            b.btnAddToCart.setOnClickListener { onAddToCart(p) }
        }
    }

    inner class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    private fun formatPrice(value: Number): String {
        val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return nf.format(value).replace(",00", "")
    }

    private fun applyBadges(p: Product, officialIcon: View?, merchantIcon: View?) {
        if (officialIcon is android.widget.ImageView) {
            val isOfficial = !p.brand.isNullOrBlank() && !p.brand.equals("no brand", true)
            officialIcon.visibility = if (isOfficial) View.VISIBLE else View.GONE
        }
        if (merchantIcon is android.widget.ImageView) {
            val url = p.badge?.merchantBadgeUrl
            if (!url.isNullOrBlank()) {
                merchantIcon.visibility = View.VISIBLE
                Glide.with(merchantIcon.context).load(url).into(merchantIcon)
            } else merchantIcon.visibility = View.GONE
        }
    }
}
