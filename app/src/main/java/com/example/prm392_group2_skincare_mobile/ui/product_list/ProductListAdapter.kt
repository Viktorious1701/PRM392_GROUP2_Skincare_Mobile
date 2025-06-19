package com.example.prm392_group2_skincare_mobile.ui.product_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse

class ProductListAdapter(
    private val onItemClick: (CosmeticResponse) -> Unit
) : ListAdapter<CosmeticResponse, ProductListAdapter.ProductViewHolder>(DiffCallback()) {

    companion object {
        private const val TAG = "ProductListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        Log.d(TAG, "Creating new ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = getItem(position)
        Log.d(TAG, "Binding item at position $position: ${item.name}")
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        Log.d(TAG, "Adapter item count: $count")
        return count
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        private val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        private val productRating: TextView = itemView.findViewById(R.id.tv_product_rating)
        private val productBrand: TextView = itemView.findViewById(R.id.tv_product_brand)

        fun bind(cosmetic: CosmeticResponse) {
            Log.d(TAG, "Binding cosmetic: ${cosmetic.name}, price: ${cosmetic.price}")
            
            // Cache formatted strings to avoid repeated operations
            val formattedPrice = "$%.2f".format(cosmetic.price)
            val formattedRating = cosmetic.rating?.let { "★ %.1f".format(it) } ?: "★ N/A"
            val brandName = cosmetic.store?.name ?: "Unknown Brand"
            
            productName.text = cosmetic.name
            productPrice.text = formattedPrice
            productRating.text = formattedRating
            productBrand.text = brandName

            Log.d(TAG, "Set text values - Name: ${cosmetic.name}, Price: $formattedPrice")

            // Optimized Glide loading
            val imageUrl = cosmetic.thumbnailUrl ?: cosmetic.cosmeticImages?.firstOrNull()?.imageUrl
            
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(productImage)

            itemView.setOnClickListener {
                Log.d(TAG, "Item clicked: ${cosmetic.name}")
                onItemClick(cosmetic)
            }
        }
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