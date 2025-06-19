package com.example.prm392_group2_skincare_mobile.data.model.request


import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("message") val message: String
)