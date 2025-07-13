// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/request/GetDistrictRequest.kt
package com.example.prm392_group2_skincare_mobile.data.model.request

import com.google.gson.annotations.SerializedName

// Request body for getting districts. Matches the backend DTO.
data class GetDistrictRequest(
    @SerializedName("province_id") val provinceId: Int
)

// Request body for getting wards.
data class GetWardRequest(
    @SerializedName("district_id") val districtId: Int
)