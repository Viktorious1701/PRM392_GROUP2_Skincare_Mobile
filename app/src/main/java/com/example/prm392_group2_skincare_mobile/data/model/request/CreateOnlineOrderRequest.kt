// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/request/CreateOnlineOrderRequest.kt
package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

// Represents the data sent to the backend to create an online order.
data class CreateOnlineOrderRequest(
    @SerializedName("shippingAddress") val shippingAddress: String,
    @SerializedName("billingAddress") val billingAddress: String,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("wardCode") val wardCode: String,
    @SerializedName("districtId") val districtId: Int,
    @SerializedName("couponId") val couponId: String? = null
)