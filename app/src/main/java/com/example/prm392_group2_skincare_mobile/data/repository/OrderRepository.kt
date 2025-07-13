// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/repository/OrderRepository.kt
package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.model.request.CreateOnlineOrderRequest
import com.example.prm392_group2_skincare_mobile.data.remote.api.OrderApiService

// Repository to abstract the order data source.
class OrderRepository(private val orderApiService: OrderApiService) {
    suspend fun createOrder(request: CreateOnlineOrderRequest) = orderApiService.createOrder(request)
}