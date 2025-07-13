// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/GHNAddressResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

// Data models to represent GHN's address structure.

data class Province(
    @SerializedName("ProvinceID") val provinceID: Int,
    @SerializedName("ProvinceName") val provinceName: String
) {
    // Override toString() to display the province name in the Spinner.
    override fun toString(): String = provinceName
}

data class District(
    @SerializedName("DistrictID") val districtID: Int,
    @SerializedName("DistrictName") val districtName: String
) {
    override fun toString(): String = districtName
}

data class Ward(
    @SerializedName("WardCode") val wardCode: String,
    @SerializedName("WardName") val wardName: String
) {
    override fun toString(): String = wardName
}