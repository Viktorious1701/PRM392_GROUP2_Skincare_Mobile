package com.example.prm392_group2_skincare_mobile.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse

class FeaturedProductAdapter(private val onItemClick: (CosmeticResponse) -> Unit) :
    ListAdapter<CosmeticResponse, FeaturedProductAdapter.ProductViewHolder>(DiffCallback()) {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.iv_product_image)
        private val nameView: TextView = view.findViewById(R.id.tv_product_name)
        private val priceView: TextView = view.findViewById(R.id.tv_product_price)

        fun bind(product: CosmeticResponse, onItemClick: (CosmeticResponse) -> Unit) {
            nameView.text = product.name
            priceView.text = String.format("%,.0f VND", product.price)

            Glide.with(itemView.context)
                .load(product.thumbnailUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(imageView)

            itemView.setOnClickListener { onItemClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_featured_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<CosmeticResponse>() {
        override fun areItemsTheSame(oldItem: CosmeticResponse, newItem: CosmeticResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CosmeticResponse, newItem: CosmeticResponse): Boolean {
            return oldItem == newItem
        }
    }
}