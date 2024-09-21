package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AlertsApi {
    @GET("/alerts")
    fun fetchAlerts(@Header("Authorization") authHeader: String): Call<AlertsResponse>

    @POST("/bookAppointment")
    fun bookAppointment(@Header("Authorization") authHeader: String, @Body appointmentBookingRequest: AppointmentBookingRequest): Call<AppointmentBookingResponse>

    @POST("/markAsRepaired")
    fun markAsRepaired(@Header("Authorization") authHeader: String, @Body markAsRepairedRequest: MarkAsRepairedRequest): Call<MarkAsRepairedResponse>

    @POST("/pollBookingStatus")
    fun pollBookingStatus(@Header("Authorization") authHeader: String, @Body pollBookingStatusRequest: PollBookingStatusRequest): Call<PollBookingStatusResponse>
}