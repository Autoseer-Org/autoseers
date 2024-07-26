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
    val data: HomeData? = null
)

@Serializable
data class HomeData(
    @SerialName("mileage")
    val mileage: Int? = 0,
    @SerialName("health_score")
    val healthScore: Int,
    @SerialName("alert")
    val alerts: Int,
    @SerialName("repairs")
    val repairs: Int,
    @SerialName("reports")
    val reports: Int,
    @SerialName("modelMake")
    val modelMake: String,
)

@Serializable
data class HomeUploadRequest(
    @SerialName("token")
    val tokenId: String,
    @Serializable
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeUploadRequest

        if (tokenId != other.tokenId) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tokenId.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}

@Serializable
data class HomeUploadResponse(
    @SerialName("status")
    val status: UploadStatus
)

@Serializable
enum class UploadStatus(status: String) {
    PARSED_FAILURE("parsed_failure"),
    PROCESSING("processing"),
    SUCCESS("success"),
    UNKNOWN("unknown");
}