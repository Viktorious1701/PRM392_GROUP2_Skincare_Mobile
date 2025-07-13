// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/billing/OrderViewModel.kt
package com.example.prm392_group2_skincare_mobile.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.data.model.request.CreateOnlineOrderRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.OrderResponse
import com.example.prm392_group2_skincare_mobile.data.repository.OrderRepository
import kotlinx.coroutines.launch

// ViewModel to manage the state and logic for order creation.
class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderResult = MutableLiveData<Result<OrderResponse>>()
    val orderResult: LiveData<Result<OrderResponse>> = _orderResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Creates an order by calling the repository and updates LiveData with the result.
    fun createOrder(request: CreateOnlineOrderRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = orderRepository.createOrder(request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        _orderResult.postValue(Result.success(apiResponse.data))
                    } else {
                        val errorMsg = apiResponse.errors?.firstOrNull()?.description ?: apiResponse.message
                        _orderResult.postValue(Result.failure(Exception(errorMsg)))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _orderResult.postValue(Result.failure(Exception("Failed to create order: $errorBody")))
                }
            } catch (e: Exception) {
                _orderResult.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}