package com.innovara.autoseers.api.onboarding

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class OnboardingServiceState {
    data object OnboardingSuccess : OnboardingServiceState()
    data class OnboardingFailed(
        val reason: String
    ) : OnboardingServiceState()
}

interface OnboardingService {
    suspend fun sendOnboardingData(token: String, onboardingRequest: OnboardingRequest): Flow<OnboardingServiceState>
}

class OnboardingServiceImpl @Inject constructor(
    retrofit: Retrofit,
) : OnboardingService {
    private val api: OnboardingApi = retrofit.create(OnboardingApi::class.java)
    override suspend fun sendOnboardingData(token: String, onboardingRequest: OnboardingRequest): Flow<OnboardingServiceState> =
        flow {
            val response = api.sendOnboardingCompletion(authHeader = token, onboardingRequest = onboardingRequest).await()
            when {
                response.reason.isNullOrBlank() -> emit(
                    OnboardingServiceState.OnboardingSuccess
                )

                else -> emit(
                    OnboardingServiceState.OnboardingFailed(response.reason ?: "")
                )
            }
        }
}