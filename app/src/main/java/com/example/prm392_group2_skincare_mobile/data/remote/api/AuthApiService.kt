package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.RegisterRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Void>>
}