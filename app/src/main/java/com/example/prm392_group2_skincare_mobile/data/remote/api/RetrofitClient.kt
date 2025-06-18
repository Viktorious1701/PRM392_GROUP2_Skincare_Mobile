package com.example.prm392_group2_skincare_mobile.data.remote

import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://localhost:8081/" // Replace with your API base URL

    val apiService: AuthApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}


