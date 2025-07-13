// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/repository/CartRepository.kt
package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.model.request.AddProductRequest
import com.example.prm392_group2_skincare_mobile.data.remote.api.CartApiService

// Repository to handle cart-related data operations, abstracting the data source from the ViewModel.
class CartRepository(private val cartApiService: CartApiService) {
    // Fetches the current user's cart from the API service.
    suspend fun getCurrentUserCart() = cartApiService.getCurrentUserCart()

    // Adds a product to the cart via the API service.
    suspend fun addItemToCart(request: AddProductRequest) = cartApiService.addItemToCart(request)
}