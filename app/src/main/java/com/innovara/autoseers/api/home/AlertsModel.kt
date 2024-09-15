package com.innovara.autoseers.api.home

import kotlinx.serialization.Serializable

@Serializable
data class AlertsResponse(
    val data: List<Alert>? = null
)

@Serializable
data class Alert(
    val id: String,
    val name: String,
    val category: String,
    val updatedDate: String,
    val status: String,
    val summary: String? = "",
    val possibleFixes: String? = ""
)