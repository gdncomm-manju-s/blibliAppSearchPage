package com.example.bliblihomepage.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.data.model.Product
import com.example.bliblihomepage.databinding.ItemLoadingBinding
import com.example.bliblihomepage.databinding.ItemProductGridBinding
import com.example.bliblihomepage.databinding.ItemProductListBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Product>()
    private var showLoading = false
    var isGrid = false

    companion object {
        private const val TYPE_LIST = 0
        private const val TYPE_GRID = 1
        private const val TYPE_LOADING = 2
    }

    override fun getItemCount(): Int = items.size + if (showLoading) 1 else 0

    override fun getItemViewType(position: Int): Int {
        if (showLoading && position == itemCount - 1) return TYPE_LOADING
        return if (isGrid) TYPE_GRID else TYPE_LIST
    }

    fun setData(list: List<Product>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun appendData(list: List<Product>) {
        val start = items.size
        items.addAll(list)
        notifyItemRangeInserted(start, list.size)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_LIST -> ListVH(ItemProductListBinding.inflate(inflater, parent, false))
            TYPE_GRID -> GridVH(ItemProductGridBinding.inflate(inflater, parent, false))
            else -> LoadingVH(ItemLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingVH) return
        if (position >= items.size) return

        when (holder) {
            is ListVH -> holder.bind(items[position])
            is GridVH -> holder.bind(items[position])
        }
    }

    // LIST ViewHolder
    inner class ListVH(private val b: ItemProductListBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(p: Product) {

            b.tvTitle.text = p.name

            val priceNum = p.price.salePrice ?: p.price.minPrice ?: 0
            b.tvPrice.text = p.price.priceDisplay ?: formatPrice(priceNum)

            // Strike-through original price
            val orig = p.price.strikeThroughPriceDisplay
            if (!orig.isNullOrEmpty()) {
                b.tvOriginalPrice.visibility = View.VISIBLE
                b.tvOriginalPrice.text = orig
                b.tvOriginalPrice.paintFlags =
                    b.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else b.tvOriginalPrice.visibility = View.GONE

            val disc = p.price.discount
            if (disc > 0) {
                b.tvDiscount.visibility = View.VISIBLE
                b.tvDiscount.text = "$disc%"
            } else b.tvDiscount.visibility = View.GONE

            val rating = p.review?.absoluteRating ?: 0.0
            val sold = p.soldCountTotal.takeIf { it > 0 } ?: p.review?.count ?: 0

            if (rating > 0.0) {

                // SHOW entire rating row
                b.ratingRow.visibility = View.VISIBLE

                b.tvRating.text = String.format(Locale.getDefault(), "%.1f", rating)
                b.tvSoldSmall.text = "Terjual $sold"

            } else {

                // HIDE ENTIRE ROW including star
                b.ratingRow.visibility = View.GONE
            }

            // Location
            b.tvLocation.text = p.location ?: ""

            // Badges
            applyBadges(p, b.imgOfficial, b.imgMerchantBadge)

            // Tags (FreeShip / CNC)
            applyTags(p, b.ivBadgeFreeShip, b.ivBadgeCnc)

            // Image
            val img = p.images.firstOrNull()
            if (!img.isNullOrEmpty())
                Glide.with(b.root).load(img).placeholder(R.drawable.placeholder_image).into(b.imgProduct)
            else b.imgProduct.setImageResource(R.drawable.placeholder_image)

            b.root.setOnClickListener { onItemClick(p) }
            b.btnAddToCart.setOnClickListener { onAddToCart(p) }
        }
    }

    // GRID ViewHolder
    inner class GridVH(private val b: ItemProductGridBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(p: Product) {
            b.tvTitle.text = p.name

            val priceNum = p.price.salePrice ?: p.price.minPrice ?: 0
            b.tvPrice.text = p.price.priceDisplay ?: formatPrice(priceNum)

            val orig = p.price.strikeThroughPriceDisplay
            if (!orig.isNullOrEmpty()) {
                b.tvOriginalPrice.visibility = View.VISIBLE
                b.tvOriginalPrice.text = orig
                b.tvOriginalPrice.paintFlags =
                    b.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else b.tvOriginalPrice.visibility = View.GONE

            val disc = p.price.discount
            if (disc > 0) {
                b.tvDiscount.visibility = View.VISIBLE
                b.tvDiscount.text = "$disc%"
            } else b.tvDiscount.visibility = View.GONE

            applyBadges(p, null, b.imgMerchantBadge)

            val img = p.images.firstOrNull()
            if (!img.isNullOrEmpty())
                Glide.with(b.root).load(img).into(b.imgProduct)
            else b.imgProduct.setImageResource(R.drawable.placeholder_image)

            b.root.setOnClickListener { onItemClick(p) }
        }
    }

    inner class LoadingVH(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun formatPrice(value: Number): String {
        val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return nf.format(value).replace(",00", "")
    }

    private fun applyBadges(
        p: Product,
        officialIcon: View?,
        merchantIcon: View?
    ) {
        val isOfficial =
            !p.brand.isNullOrEmpty() && !p.brand.equals("no brand", ignoreCase = true)

        if (officialIcon is android.widget.ImageView)
            officialIcon.visibility = if (isOfficial) View.VISIBLE else View.GONE

        val url = p.badge?.merchantBadgeUrl
        if (merchantIcon is android.widget.ImageView) {
            if (!url.isNullOrEmpty()) {
                merchantIcon.visibility = View.VISIBLE
                Glide.with(merchantIcon.context).load(url).into(merchantIcon)
            } else merchantIcon.visibility = View.GONE
        }
    }

    private fun applyTags(
        p: Product,
        freeShipIcon: android.widget.ImageView,
        cncIcon: android.widget.ImageView
    ) {
        val tags = p.tags.map { it.uppercase() }

        freeShipIcon.visibility =
            if ("FREE_SHIPPING" in tags) View.VISIBLE else View.GONE

        cncIcon.visibility =
            if ("CNC_AVAILABLE" in tags || "CLICK_COLLECT" in tags) View.VISIBLE else View.GONE
    }
}
