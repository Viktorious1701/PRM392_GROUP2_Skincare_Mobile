// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/data/model/response/PaginatedList.kt
package com.example.prm392_group2_skincare_mobile.data.model.response

import com.google.gson.annotations.SerializedName

data class PaginatedList<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("hasPreviousPage") val hasPreviousPage: Boolean,
    @SerializedName("hasNextPage") val hasNextPage: Boolean
)