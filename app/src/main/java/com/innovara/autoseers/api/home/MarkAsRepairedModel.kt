package com.innovara.autoseers.api.home

import kotlinx.serialization.Serializable

@Serializable
data class MarkAsRepairedRequest(
    val partId: String
)

@Serializable
data class MarkAsRepairedResponse(
    val failure: String? = null
)