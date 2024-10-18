package com.innovara.autoseers.api.onboarding

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OnboardingApi {
    @POST("createUserProfile")
    fun sendOnboardingCompletion(
        @Header("Authorization") authHeader: String,
        @Body onboardingRequest: OnboardingRequest
    ): Call<OnboardingResponse>

    @GET("deleteAccount")
    fun deleteAccount(
        @Header("Authorization") authHeader: String,
    ): Call<DeleteAccountModel>
}