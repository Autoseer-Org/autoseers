package com.innovara.autoseers.api.home

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApi {
    @POST("/upload")
    fun uploadReport(@Body uploadRequest: HomeUploadRequest): Call<HomeUploadResponse>

    @POST("/home")
    fun fetchHomeData(@Body homeRequest: HomeRequest): Call<HomeResponse>
}