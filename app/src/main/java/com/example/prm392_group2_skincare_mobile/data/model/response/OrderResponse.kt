// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/OrderResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

// Represents the response from the backend after an order is created.
// It contains the crucial paymentUrl for the VNPay gateway.
data class OrderResponse(
    @SerializedName("id") val id: String,
    @SerializedName("paymentUrl") val paymentUrl: String?
)