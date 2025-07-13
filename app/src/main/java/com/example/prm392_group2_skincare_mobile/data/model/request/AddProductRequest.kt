// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/request/AddProductRequest.kt
package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

// Data class to represent the request body for adding a product to the cart.
data class AddProductRequest(
    @SerializedName("cosmeticId") val cosmeticId: String,
    @SerializedName("quantity") val quantity: Int
)