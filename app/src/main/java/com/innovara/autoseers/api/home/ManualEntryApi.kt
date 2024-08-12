package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ManualEntryApi {
    @POST("/manualEntry")
    fun manuallyEnterCarData(@Body manualEntryRequest: ManualEntryRequest): Call<ManualEntryResponse>
}