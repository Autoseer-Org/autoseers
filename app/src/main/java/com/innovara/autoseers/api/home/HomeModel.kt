package com.innovara.autoseers.api.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val mileage: Int? = 0,
    @SerialName("health_score")
    val healthScore: Int,
    @SerialName("alerts")
    val alerts: Int,
    @SerialName("recalls")
    val recalls: Int? = null,
    @SerialName("repairs")
    val repairs: Int,
    @SerialName("reports")
    val reports: Int,
    @SerialName("make")
    val make: String? = null,
    @SerialName("model")
    val model: String? = null,
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