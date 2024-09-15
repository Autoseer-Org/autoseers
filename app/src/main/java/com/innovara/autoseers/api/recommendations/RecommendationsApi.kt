package com.innovara.autoseers.api.recommendations

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RecommendationsApi {
    @GET("/recommendations")
    fun fetchRecommendations(@Header("Authorization") authHeader: String): Call<RecommendationsResponse>

    @POST("/manualEntry")
    fun manualEntryForCarInfo(@Header("Authorization") authHeader: String, @Body manualEntryRequest: ManualEntryRequest): Call<ManualEntryResponse>
}