// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/RetrofitClient.kt
package com.example.prm392_group2_skincare_mobile.data.remote

import com.example.prm392_group2_skincare_mobile.data.remote.api.AuthApiService
import com.example.prm392_group2_skincare_mobile.data.remote.api.ChatAIApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // For local development with Android Emulator, use 10.0.2.2 to connect to your PC's localhost.
    // The backend is running in a Docker container that exposes port 8080 to the host's port 8082.
    // Therefore, the emulator must connect to port 8082 on the host.
    private const val BASE_URL = "http://10.0.2.2:8082/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Generic function to create any service
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    // Existing services for backward compatibility
    val chatAIApiService: ChatAIApiService by lazy {
        retrofit.create(ChatAIApiService::class.java)
    }

    val apiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}