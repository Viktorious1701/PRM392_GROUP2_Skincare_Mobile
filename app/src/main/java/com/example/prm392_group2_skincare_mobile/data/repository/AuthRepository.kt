package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.RegisterRequest
import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService

class AuthRepository(private val authApiService: AuthApiService) {

    suspend fun login(loginRequest: LoginRequest) = authApiService.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = authApiService.register(registerRequest)
}