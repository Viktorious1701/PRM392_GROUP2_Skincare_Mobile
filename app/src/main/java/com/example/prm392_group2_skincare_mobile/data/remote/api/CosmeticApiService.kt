// app/src/main/java/com/example/prm392_group2_skincare_mobile/data/remote/api/CosmeticApiService.kt
package com.example.prm392_group2_skincare_mobile.data.remote.api

import com.example.prm392_group2_skincare_mobile.data.model.response.ApiResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse
import com.example.prm392_group2_skincare_mobile.data.model.response.PaginatedList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CosmeticApiService {

    @GET("api/cosmetics")
    suspend fun getCosmetics(
        @Query("name") name: String? = null,
        @Query("brandId") brandId: String? = null,
        @Query("skinTypeId") skinTypeId: String? = null,
        @Query("cosmeticTypeId") cosmeticTypeId: String? = null,
        @Query("gender") gender: Boolean? = null,
        @Query("sortColumn") sortColumn: String? = null,
        @Query("sortOrder") sortOrder: String = "asc",
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<ApiResponse<PaginatedList<CosmeticResponse>>>

    @GET("api/cosmetics/{id}")
    suspend fun getCosmeticById(
        @Path("id") id: String
    ): Response<ApiResponse<CosmeticResponse>>
}