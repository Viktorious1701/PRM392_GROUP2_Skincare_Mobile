package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("gender") val gender: Boolean,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("password") val password: String,
    @SerializedName("passwordConfirmation") val passwordConfirmation: String
)