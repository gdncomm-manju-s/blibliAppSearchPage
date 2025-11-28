package com.example.bliblihomepage.ui.productListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bliblihomepage.R
import com.example.bliblihomepage.databinding.ItemBannerBinding

class BannerAdapter(
    private var items: MutableList<String>
) : RecyclerView.Adapter<BannerAdapter.BannerVH>() {

    inner class BannerVH(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerVH {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerVH(binding)
    }

    override fun onBindViewHolder(holder: BannerVH, position: Int) {
        val url = items[position]
        Glide.with(holder.itemView)
            .load(url)
            .into(holder.binding.imgBanner)
    }

    override fun getItemCount() = items.size

    fun update(newList: List<String>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
