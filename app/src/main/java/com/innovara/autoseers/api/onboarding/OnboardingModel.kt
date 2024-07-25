package com.innovara.autoseers.api.onboarding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingRequest(
    @SerialName("name")
    val userName: String,
    @SerialName("token")
    val tokenId: String,
)

@Serializable
data class OnboardingResponse(
    @SerialName("failure")
    val reason: String? = null,
)