package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AlertsApi {
    @POST("/alerts")
    fun fetchAlerts(@Body alertsRequest: AlertsRequest): Call<AlertsResponse>
}