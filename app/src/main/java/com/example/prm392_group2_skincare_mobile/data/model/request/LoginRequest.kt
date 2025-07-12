package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("password")
    val password: String
)