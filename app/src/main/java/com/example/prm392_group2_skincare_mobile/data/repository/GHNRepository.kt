// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/repository/GHNRepository.kt
package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.model.request.GetDistrictRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.GetWardRequest
import com.example.prm392_group2_skincare_mobile.data.remote.api.GHNApiService

// Repository for fetching address data from the GHN API.
class GHNRepository(private val ghnApiService: GHNApiService) {
    suspend fun getProvinces() = ghnApiService.getProvinces()

    // Updated to pass the request object.
    suspend fun getDistricts(request: GetDistrictRequest) = ghnApiService.getDistricts(request)

    // Updated to pass the request object.
    suspend fun getWards(request: GetWardRequest) = ghnApiService.getWards(request)
}