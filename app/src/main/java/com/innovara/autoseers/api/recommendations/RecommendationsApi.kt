package com.innovara.autoseers.api.recommendations

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationsApi {
    @POST("/recommendations")
    fun fetchRecommendations(@Body recommendationsRequest: RecommendationsRequest): Call<RecommendationsResponse>

    @POST("/manualEntry")
    fun manualEntryForCarInfo(@Body manualEntryRequest: ManualEntryRequest): Call<ManualEntryResponse>
}