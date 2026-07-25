package com.example.data.database.entities

data class CatalogSearchResult(
    val variantId: Long,
    val modelId: Long,
    val manufacturerId: Long,
    val brandName: String,
    val modelName: String,
    val variantName: String,
    val category: String,
    val bodyType: String,
    val fuelType: String,
    val transmission: String,
    val approxExShowroomPrice: Long,
    val approxOnRoadPrice: Long,
    val launchYear: Int,
    val popularityScore: Int
)
