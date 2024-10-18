package com.innovara.autoseers.api.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountModel(
    val success: Boolean,
)