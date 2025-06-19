package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("id") val id: String,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("originalTotalPrice") val originalTotalPrice: Double,
    @SerializedName("items") val items: List<CartItem>
)

data class CartItem(
    @SerializedName("cosmeticId") val cosmeticId: String,
    @SerializedName("cosmeticName") val cosmeticName: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("discountedPrice") val discountedPrice: Double,
    @SerializedName("quantity") val quantity: Int
)