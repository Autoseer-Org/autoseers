package com.innovara.autoseers.home.model

data class RecallModel(
    val nhtsaCampaignNumber: String,
    val manufacturer:  String,
    val reportReceivedDate: String,
    val component: String,
    val summary: String,
    val consequence: String,
    val remedy: String,
    val notes: String,
    val status: String
)