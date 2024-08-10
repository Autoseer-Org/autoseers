package com.innovara.autoseers.api.home

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentBookingRequest(
    val token: String,
    val place: String,
    val timeDate: String,
    val email: String,
)

@Serializable
data class AppointmentBookingResponse(
    val failure: String?,
)

@Serializable
data class MarkAsRepairRequest(
    val token: String,
    val partName: String,
)

@Serializable
data class MarkAsRepairResponse(
    val failure: String?
)