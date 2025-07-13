// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/request/RefreshTokenRequest.kt
package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

// Request body for the token refresh endpoint.
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)