// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/AuthApiService.kt
package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.RefreshTokenRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.RegisterRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Void>>

    // Synchronous call for token refresh, to be used by the Authenticator.
    @POST("api/auth/refresh-token")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<ApiResponse<LoginResponse>>
}