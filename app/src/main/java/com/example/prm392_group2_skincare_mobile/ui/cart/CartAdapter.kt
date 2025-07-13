// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/cart/CartAdapter.kt
package com.example.prm392_group2_skincare_mobile.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.response.CartItem

// Adapter for the RecyclerView in the CartActivity, responsible for displaying cart items.
class CartAdapter(private var items: List<CartItem>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder holds the views for a single cart item.
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_cart_item_image)
        private val nameView: TextView = itemView.findViewById(R.id.tv_cart_item_name)
        private val priceView: TextView = itemView.findViewById(R.id.tv_cart_item_price)
        private val quantityView: TextView = itemView.findViewById(R.id.tv_cart_item_quantity)

        // Binds a CartItem object to the views.
        fun bind(item: CartItem) {
            nameView.text = item.cosmeticName
            priceView.text = String.format("%,.0f VND", item.price)
            quantityView.text = String.format("Qty: %d", item.quantity)
            // Use Glide to load the product image.
            Glide.with(itemView.context).load(item.thumbnailUrl).into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // Updates the list of items and refreshes the RecyclerView.
    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}