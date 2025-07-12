package com.example.prm392_group2_skincare_mobile.ui.product_detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse
import com.example.prm392_group2_skincare_mobile.data.remote.RetrofitClient
import com.example.prm392_group2_skincare_mobile.ui.map.MapActivity
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
    // ADDED: Declare the button view
    private lateinit var viewOnMapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Product Details"

        initViews()

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            showError("Product ID not found")
            finish()
        }
    }

    private fun initViews() {
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
        // ADDED: Initialize the button
        viewOnMapButton = findViewById(R.id.button_view_on_map)
    }

    private fun loadProductDetails(productId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.cosmeticApiService.getCosmeticById(productId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        val product = apiResponse.data
                        if (product != null) {
                            displayProductDetails(product)
                        }
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
        productName.text = product.name
        productPrice.text = "$${product.price}"
        productRating.text = "★ ${product.rating?.let { "%.1f".format(it) } ?: "N/A"}"
        productBrand.text = product.store?.name ?: "Unknown Brand"
        productDescription.text = product.notice
        productIngredients.text = product.ingredients
        productInstructions.text = product.instructions
        productMainUsage.text = product.mainUsage
        productTexture.text = product.texture
        productOrigin.text = product.origin

        // ADDED: Logic to show the button and handle click event
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

        // Load product image
        Glide.with(this)
            .load(product.thumbnailUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(productImage)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}