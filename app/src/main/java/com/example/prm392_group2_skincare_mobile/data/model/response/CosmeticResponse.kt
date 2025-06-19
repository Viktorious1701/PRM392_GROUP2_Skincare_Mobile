// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/CosmeticResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class CosmeticResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?,
    @SerializedName("store") val store: StoreResponse?
)