package com.innovara.autoseers.api.recommendations

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationsRequest(
    val token: String,
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