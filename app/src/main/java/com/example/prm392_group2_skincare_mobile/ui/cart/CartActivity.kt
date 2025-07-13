// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/cart/CartActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.CartApiService
import com.example.prm392_group2_skincare_mobile.data.repository.CartRepository
import com.example.prm392_group2_skincare_mobile.databinding.ActivityCartBinding
import com.example.prm392_group2_skincare_mobile.ui.billing.BillingActivity

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val viewModel: CartViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val cartService = RetrofitClient.create(CartApiService::class.java)
                val repository = CartRepository(cartService)
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar with a title and back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Your Cart"

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Fetch cart data when the activity is created
        viewModel.fetchCurrentUserCart()
    }

    // Handles the click on the back button in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list
        cartAdapter = CartAdapter(emptyList())
        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)
        }
    }

    private fun setupObservers() {
        // Observe the loading state to show/hide the progress bar
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarCart.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe the cart data to update the UI
        viewModel.cart.observe(this) { result ->
            result.onSuccess { cartResponse ->
                if (cartResponse.items.isEmpty()) {
                    binding.tvEmptyCart.visibility = View.VISIBLE
                    binding.rvCartItems.visibility = View.GONE
                } else {
                    binding.tvEmptyCart.visibility = View.GONE
                    binding.rvCartItems.visibility = View.VISIBLE
                    // Update the adapter with the new list of items
                    cartAdapter.updateItems(cartResponse.items)
                }
                // Update the total price
                binding.tvTotalPrice.text = String.format("Total: $%.2f", cartResponse.totalPrice)
            }.onFailure { exception ->
                // Show an error message if fetching the cart fails
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                binding.tvEmptyCart.visibility = View.VISIBLE
                binding.rvCartItems.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        // Set up the checkout button to navigate to the BillingActivity
        binding.buttonCheckout.setOnClickListener {
            startActivity(Intent(this, BillingActivity::class.java))
        }
    }
}