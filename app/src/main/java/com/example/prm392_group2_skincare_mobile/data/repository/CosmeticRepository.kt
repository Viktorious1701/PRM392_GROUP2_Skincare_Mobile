package com.example.prm392_group2_skincare_mobile.data.repository

import com.example.prm392_group2_skincare_mobile.data.remote.api.CosmeticApiService

class CosmeticRepository(private val cosmeticApiService: CosmeticApiService) {
    suspend fun getCosmetics(pageIndex: Int, pageSize: Int) =
        cosmeticApiService.getCosmetics(pageIndex = pageIndex, pageSize = pageSize)
}