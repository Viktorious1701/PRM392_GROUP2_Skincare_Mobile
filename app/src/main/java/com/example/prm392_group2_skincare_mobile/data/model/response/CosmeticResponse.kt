// app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/CosmeticResponse.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CosmeticResponse(
    val id: String,
    val createAt: Date?,
    val createdBy: String?,
    val lastModified: Date?,
    val lastModifiedBy: String?,
    val isActive: Boolean,
    val brandId: String,
    val skinTypeId: String,
    val cosmeticTypeId: String,
    val name: String,
    val price: Double,
    val originalPrice: Double,
    val gender: Boolean,
    val notice: String,
    val ingredients: String,
    val mainUsage: String,
    val texture: String,
    val origin: String,
    val instructions: String,
    val weight: Int,
    val length: Int,
    val width: Int,
    val height: Int,
    val thumbnailUrl: String?,
    val volumeUnit: Int, // Corrected from String to Int, as JSON returns 0
    val store: StoreResponse?,
    val cosmeticSubcategories: List<CosmeticSubcategoryResponse>?,
    val cosmeticImages: List<CosmeticImageResponse>?,
    val feedbacks: List<FeedbackResponse>?,
    val batches: List<BatchResponse>?,
    val quantity: Int,
    val rating: Double?
)

data class CosmeticSubcategoryResponse(
    val cosmeticId: String,
    val subCategoryId: String,
    val subCategory: SubCategoryResponse? // subCategory can be null
)

data class SubCategoryResponse(
    val id: String,
    val name: String,
    val description: String
)

data class CosmeticImageResponse(
    val id: String,
    val cosmeticId: String,
    val cosmeticName: String?,
    val imageUrl: String
)

data class FeedbackResponse(
    val id: String,
    val customer: CustomerResponse?, // customer can be null
    @SerializedName("content") val content: String?, // Mapped from "content" in JSON
    val rating: Double
)

data class CustomerResponse(
    val id: String?,
    val userName: String?
)

data class BatchResponse(
    val id: String,
    val quantity: Int,
    val expirationDate: String
)