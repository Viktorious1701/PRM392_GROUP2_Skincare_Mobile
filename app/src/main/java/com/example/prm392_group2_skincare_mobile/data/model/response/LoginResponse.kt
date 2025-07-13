package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userName") val userName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("accessTokenExpiration") val accessTokenExpiration: Long,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("refreshTokenExpiration") val refreshTokenExpiration: Long
)