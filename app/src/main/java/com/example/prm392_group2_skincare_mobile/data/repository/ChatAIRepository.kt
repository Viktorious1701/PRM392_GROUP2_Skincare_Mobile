package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.model.request.ChatRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.ChatResponse
import com.example.prm392_group2_skincare_mobile.data.remote.api.ChatAIApiService
import retrofit2.Response

class ChatAIRepository(private val apiService: ChatAIApiService) {
    suspend fun sendMessage(message: String): Response<ApiResponse<ChatResponse>> {
        val request = ChatRequest(message)
        return apiService.sendMessage(request)
    }
}