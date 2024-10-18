package com.innovara.autoseers.api.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeRequest(
    @SerialName("token")
    val tokenId: String,
)

@Serializable
data class HomeResponse(
    @SerialName("data")
    val data: HomeData? = null,
    @SerialName("failure")
    val failure: String? = null
)

@Serializable
data class HomeData(
    @SerialName("mileage")
    val mileage: String? = "",
    @SerialName("health_score")
    val healthScore: Int,
    @SerialName("alerts")
    val alerts: Int,
    @SerialName("recalls")
    val recalls: Int? = 0,
    @SerialName("repairs")
    val repairs: Int,
    @SerialName("reports")
    val reports: Int,
    @SerialName("make")
    val make: String? = null,
    @SerialName("model")
    val model: String? = null,
    @SerialName("estimatedCarPrice")
    val estimatedCarPrice: String? = null
)

@Serializable
data class HomeUploadRequest(
    @SerialName("image")
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeUploadRequest
        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        return image.contentHashCode()
    }
}

@Serializable
data class HomeUploadResponse(
    @SerialName("failure")
    val failure: String? = null
)