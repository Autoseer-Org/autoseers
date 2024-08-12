package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AlertsApi {
    @POST("/alerts")
    fun fetchAlerts(@Body alertsRequest: AlertsRequest): Call<AlertsResponse>

    @POST("/bookAppointment")
    fun bookAppointment(@Body appointmentBookingRequest: AppointmentBookingRequest): Call<AppointmentBookingResponse>

    @POST("/markAsRepaired")
    fun markAsRepaired(@Body markAsRepairedRequest: MarkAsRepairedRequest): Call<MarkAsRepairedResponse>

    @POST("/pollBookingStatus")
    fun pollBookingStatus(@Body pollBookingStatusRequest: PollBookingStatusRequest): Call<PollBookingStatusResponse>
}