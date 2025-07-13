// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/product_detail/ProductDetailActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.product_detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.local.preferences.UserPreferences
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.data.remote.api.CartApiService
import com.example.prm392_group2_skincare_mobile.data.repository.CartRepository
import com.example.prm392_group2_skincare_mobile.ui.auth.LoginActivity
import com.example.prm392_group2_skincare_mobile.ui.cart.CartViewModel
import com.example.prm392_group2_skincare_mobile.ui.map.MapActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productRating: TextView
    private lateinit var productBrand: TextView
    private lateinit var productDescription: TextView
    private lateinit var productIngredients: TextView
    private lateinit var productInstructions: TextView
    private lateinit var productMainUsage: TextView
    private lateinit var productTexture: TextView
    private lateinit var productOrigin: TextView
    private lateinit var viewOnMapButton: Button
    private lateinit var addToCartButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout


    private var currentProduct: CosmeticResponse? = null

    // ViewModel for handling cart operations
    private val cartViewModel: CartViewModel by viewModels {
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
        setContentView(R.layout.activity_product_detail)

        initViews()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = " " // Set an empty title initially

        setupObservers()

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            showError("Product ID not found")
            finish()
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)
        productImage = findViewById(R.id.iv_product_detail_image)
        productName = findViewById(R.id.tv_product_detail_name)
        productPrice = findViewById(R.id.tv_product_detail_price)
        productRating = findViewById(R.id.tv_product_detail_rating)
        productBrand = findViewById(R.id.tv_product_detail_brand)
        productDescription = findViewById(R.id.tv_product_detail_description)
        productIngredients = findViewById(R.id.tv_product_detail_ingredients)
        productInstructions = findViewById(R.id.tv_product_detail_instructions)
        productMainUsage = findViewById(R.id.tv_product_detail_main_usage)
        productTexture = findViewById(R.id.tv_product_detail_texture)
        productOrigin = findViewById(R.id.tv_product_detail_origin)
        viewOnMapButton = findViewById(R.id.button_view_on_map)
        addToCartButton = findViewById(R.id.button_add_to_cart)
    }

    // Observe the result of adding an item to the cart.
    private fun setupObservers() {
        cartViewModel.addToCartResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Failed to add item: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadProductDetails(productId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.cosmeticApiService.getCosmeticById(productId)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        currentProduct = apiResponse.data
                        currentProduct?.let { displayProductDetails(it) }
                    } else {
                        showError("Failed to load product: ${apiResponse?.message}")
                    }
                } else {
                    showError("Failed to load product: ${response.message()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun displayProductDetails(product: CosmeticResponse) {
        collapsingToolbar.title = product.name
        productName.text = product.name
        productPrice.text = String.format("%,.0f VND", product.price)
        productRating.text = "★ ${product.rating?.let { "%.1f".format(it) } ?: "N/A"}"
        productBrand.text = product.store?.name ?: "Unknown Brand"
        productDescription.text = product.notice
        productIngredients.text = product.ingredients
        productInstructions.text = product.instructions
        productMainUsage.text = product.mainUsage
        productTexture.text = product.texture
        productOrigin.text = product.origin

        if (product.store != null) {
            viewOnMapButton.visibility = View.VISIBLE
            viewOnMapButton.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java).apply {
                    putExtra("STORE_NAME", product.store.name)
                    putExtra("LATITUDE", product.store.latitude)
                    putExtra("LONGITUDE", product.store.longitude)
                }
                startActivity(intent)
            }
        } else {
            viewOnMapButton.visibility = View.GONE
        }

        addToCartButton.setOnClickListener {
            handleAddToCart()
        }

        Glide.with(this)
            .load(product.thumbnailUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(productImage)
    }

    // Handles the "Add to Cart" button click.
    private fun handleAddToCart() {
        if (UserPreferences.isLoggedIn()) {
            currentProduct?.let {
                // Add 1 item to the cart by default.
                cartViewModel.addItemToCart(it.id, 1)
            }
        } else {
            // If the user is not logged in, prompt them to log in.
            Toast.makeText(this, "Please log in to add items to your cart.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}