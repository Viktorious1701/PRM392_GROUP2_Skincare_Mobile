// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/CartApiService.kt
package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.AddProductRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.CartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

// Defines the API endpoints related to the shopping cart.
interface CartApiService {
    // Retrieves the cart for the currently authenticated user.
    @GET("api/cart/me")
    suspend fun getCurrentUserCart(): Response<ApiResponse<CartResponse>>

    // Adds an item to the current user's cart.
    @PUT("api/cart/me/items")
    suspend fun addItemToCart(@Body request: AddProductRequest): Response<ApiResponse<List<CartResponse>>>
}