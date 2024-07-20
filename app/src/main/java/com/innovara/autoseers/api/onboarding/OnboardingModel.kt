package com.innovara.autoseers.api.onboarding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingRequest(
    @SerialName("token_id")
    val tokenId: String,
    @SerialName("user_name")
    val userName: String,
)

@Serializable
data class OnboardingResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("reason")
    val reason: String,
)