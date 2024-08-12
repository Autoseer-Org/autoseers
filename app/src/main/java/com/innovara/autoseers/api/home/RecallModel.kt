package com.innovara.autoseers.api.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecallsResponse(
    val count: Int? = null,
    var recalls: List<RecallItem>? = null,
    val failure: String? = null
)

@Serializable
data class RecallItem(
    val shortSummary: String,
    @SerialName("nhtsa_campaign_number")
    val nhtsaCampaignNumber: String,
    val manufacturer:  String,
    @SerialName("report_received_date")
    val reportReceivedDate: String,
    val component: String,
    val summary: String,
    val consequence: String,
    val remedy: String,
    val notes: String,
    val status: String
)

@Serializable
data class CompleteRecallRequest(
    val token: String,
    @SerialName("nhtsa_campaign_number")
    val nhtsaCampaignNumber: String,
)

@Serializable
data class CompleteRecallResponse(
    val failure: String? = null,
)