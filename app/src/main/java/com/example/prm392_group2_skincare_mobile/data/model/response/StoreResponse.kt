// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/StoreResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class StoreResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("openingHours") val openingHours: String?
)