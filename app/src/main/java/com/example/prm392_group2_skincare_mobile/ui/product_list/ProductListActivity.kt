// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/product_list/ProductListActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.product_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.ui.auth.LoginActivity
import com.example.prm392_group2_skincare_mobile.ui.cart.CartActivity
import com.example.prm392_group2_skincare_mobile.ui.product_detail.ProductDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductListAdapter
    private lateinit var progressBar: ProgressBar
    private var currentPage = 1
    private val pageSize = 10

    companion object {
        private const val TAG = "ProductListActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Products"

        setupViews()
        setupRecyclerView()
        loadProducts()
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.rv_products)
    }

    private fun setupRecyclerView() {
        adapter = ProductListAdapter { cosmetic ->
            openProductDetail(cosmetic)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = this@ProductListActivity.adapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }

        Log.d(TAG, "RecyclerView setup completed")
    }

    private fun loadProducts() {
        Log.d(TAG, "Starting to load products...")
        showLoading(true)

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Making API call...")

                // Make API call on IO thread
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.cosmeticApiService.getCosmetics(
                        pageIndex = currentPage,
                        pageSize = pageSize
                    )
                }

                Log.d(TAG, "API response received. Success: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d(TAG, "API response body: isSuccess=${apiResponse?.isSuccess}, message=${apiResponse?.message}")

                    if (apiResponse?.isSuccess == true) {
                        val products = apiResponse.data?.items ?: emptyList()
                        Log.d(TAG, "Products received: ${products.size} items")

                        // Process data on background thread
                        val processedProducts = withContext(Dispatchers.Default) {
                            products.map { cosmetic ->
                                Log.d(TAG, "Processing product: ${cosmetic.name}")
                                cosmetic
                            }
                        }

                        // Update UI on main thread
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "Updating RecyclerView with ${processedProducts.size} products")
                            adapter.submitList(processedProducts)
                            showLoading(false)

                            if (processedProducts.isEmpty()) {
                                showError("No products found")
                            }
                        }
                    } else {
                        showLoading(false)
                        showError("Failed to load products: ${apiResponse?.message}")
                    }
                } else {
                    showLoading(false)
                    showError("Failed to load products: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading products", e)
                showLoading(false)
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun openProductDetail(cosmetic: CosmeticResponse) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", cosmetic.id)
        startActivity(intent)
    }

    private fun showError(message: String) {
        Log.e(TAG, "Showing error: $message")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_cart -> {
                // Check if user is logged in before opening the cart
                if (UserPreferences.isLoggedIn()) {
                    startActivity(Intent(this, CartActivity::class.java))
                } else {
                    // Prompt guest to log in
                    Toast.makeText(this, "Please log in to view your cart", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}