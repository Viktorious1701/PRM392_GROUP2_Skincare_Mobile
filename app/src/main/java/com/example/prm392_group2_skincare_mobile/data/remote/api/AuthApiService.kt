package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.LoginRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest) : Call<LoginResponse>
}