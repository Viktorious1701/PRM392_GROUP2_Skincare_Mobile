// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/cart/CartViewModel.kt
package com.example.prm392_group2_skincare_mobile.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.data.model.request.AddProductRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.CartResponse
import com.example.prm392_group2_skincare_mobile.data.repository.CartRepository
import kotlinx.coroutines.launch

// ViewModel for the CartActivity, managing UI-related data and business logic.
class CartViewModel(private val repository: CartRepository) : ViewModel() {

    // LiveData to hold the cart data or any exceptions.
    private val _cart = MutableLiveData<Result<CartResponse>>()
    val cart: LiveData<Result<CartResponse>> = _cart

    // LiveData to track the loading state.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for the result of adding an item to the cart.
    private val _addToCartResult = MutableLiveData<Result<Unit>>()
    val addToCartResult: LiveData<Result<Unit>> = _addToCartResult

    // Fetches the current user's cart data from the repository.
    fun fetchCurrentUserCart() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Call the repository to get the cart data.
                val response = repository.getCurrentUserCart()
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        _cart.postValue(Result.success(apiResponse.data))
                    } else {
                        val errorMsg = apiResponse.errors?.firstOrNull()?.description ?: apiResponse.message
                        _cart.postValue(Result.failure(Exception(errorMsg)))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _cart.postValue(Result.failure(Exception("Failed to fetch cart: $errorBody")))
                }
            } catch (e: Exception) {
                // Post failure result if an exception occurs.
                _cart.postValue(Result.failure(e))
            } finally {
                // Ensure loading state is turned off.
                _isLoading.postValue(false)
            }
        }
    }

    // Adds a product to the user's cart.
    fun addItemToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val request = AddProductRequest(cosmeticId = productId, quantity = quantity)
                val response = repository.addItemToCart(request)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _addToCartResult.postValue(Result.success(Unit))
                } else {
                    val errorMsg = response.body()?.errors?.firstOrNull()?.description ?: "Failed to add item"
                    _addToCartResult.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: Exception) {
                _addToCartResult.postValue(Result.failure(e))
            }
        }
    }
}