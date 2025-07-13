package com.example.prm392_group2_skincare_mobile.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.RegisterRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.LoginResponse
import com.example.prm392_group2_skincare_mobile.data.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(loginRequest: LoginRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = authRepository.login(loginRequest)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.isSuccess && apiResponse.data != null) {
                        _loginResult.postValue(Result.success(apiResponse.data))
                    } else {
                        val errorMsg = apiResponse.errors?.firstOrNull()?.description ?: apiResponse.message
                        _loginResult.postValue(Result.failure(Exception(errorMsg)))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = errorBody ?: response.message()
                    _loginResult.postValue(Result.failure(Exception("Login failed: $message")))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = authRepository.register(registerRequest)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.isSuccess) {
                        _registerResult.postValue(Result.success(apiResponse.message))
                    } else {
                        val errorMsg = apiResponse.errors?.firstOrNull()?.description ?: apiResponse.message
                        _registerResult.postValue(Result.failure(Exception(errorMsg)))
                    }
                } else {
                     val errorBody = response.errorBody()?.string()
                    val message = errorBody ?: response.message()
                    _registerResult.postValue(Result.failure(Exception("Registration failed: $message")))
                }
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}