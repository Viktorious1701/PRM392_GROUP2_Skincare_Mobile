// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/GHNApiService.kt
package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.request.GetDistrictRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.GetWardRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.District
import com.example.prm392_group2_skincare_mobile.data.model.response.Province
import com.example.prm392_group2_skincare_mobile.data.model.response.Ward
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Defines API endpoints for fetching location data from GHN.
interface GHNApiService {
    @GET("api/ghn/get-province")
    suspend fun getProvinces(): Response<ApiResponse<List<Province>>>

    // Corrected to a POST request with a request body.
    @POST("api/ghn/get-district")
    suspend fun getDistricts(@Body request: GetDistrictRequest): Response<ApiResponse<List<District>>>

    // Corrected to a POST request with a request body.
    @POST("api/ghn/get-ward")
    suspend fun getWards(@Body request: GetWardRequest): Response<ApiResponse<List<Ward>>>
}