// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/OrderApiService.kt
package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.CreateOnlineOrderRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApiService {
    // Defines the HTTP POST request to create an order.
    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOnlineOrderRequest): Response<ApiResponse<OrderResponse>>
}