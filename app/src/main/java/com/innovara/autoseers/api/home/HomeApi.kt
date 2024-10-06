package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface HomeApi {
    @POST("/upload")
    fun uploadReport(@Header("Authorization") authHeader: String, @Body uploadRequest: HomeUploadRequest): Call<HomeUploadResponse>

    @GET("/home")
    fun fetchHomeData(@Header("Authorization") authHeader: String): Call<HomeResponse>

    @POST("/revokeTokens")
    fun revokeTokens(@Header("Authorization") authHeader: String): Call<HomeResponse>
}