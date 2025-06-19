package com.example.prm392_group2_skincare_mobile.data.model.response

data class ApiResponse<T>(
    val data: T?,
    val message: String,
    val success: Boolean,
    val errors: List<ApiError>?
)

data class ApiError(
    val code: String,
    val description: String
)