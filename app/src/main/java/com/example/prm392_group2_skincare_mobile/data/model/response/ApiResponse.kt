// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/ApiResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String,
    @SerializedName("isSuccess") val isSuccess: Boolean, // Renamed to match the JSON and added annotation
    @SerializedName("errors") val errors: List<ApiError>?
)

data class ApiError(
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String
)