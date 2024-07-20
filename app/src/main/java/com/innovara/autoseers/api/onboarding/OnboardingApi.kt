package com.innovara.autoseers.api.onboarding

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface OnboardingApi {
    @POST
    fun sendOnboardingCompletion(
        @Body onboardingRequest: OnboardingRequest
    ): Call<OnboardingResponse>
}