package com.innovara.autoseers.api.recommendations

import kotlinx.serialization.Serializable


@Serializable
data class ManualEntryRequest(
    val year: String,
    val make: String,
    val model: String,
    val mileage: String,
)

@Serializable
data class ManualEntryResponse(
    val failure: String? = null,
)

@Serializable
data class RecommendationsResponse(
    val error: String? = null,
    val data: RecommendationsList? = null
)

@Serializable
data class RecommendationsList(
    val recommendations: List<Recommendation>
)

@Serializable
data class Recommendation(
    val serviceName: String,
    val averagePrice: String,
    val description: String,
    val frequency: String,
    val priority: Int,
)