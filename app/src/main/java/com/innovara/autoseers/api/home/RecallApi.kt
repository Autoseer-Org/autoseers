package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RecallApi {
    @GET("/recalls")
    fun fetchRecalls(@Header("Authorization") authorizationToken: String): Call<RecallsResponse>

    @POST("/completeRecall")
    fun completeRecall(@Body completeRecallRequest: CompleteRecallRequest): Call<CompleteRecallResponse>
}