package com.innovara.autoseers.api.onboarding

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
    suspend fun deleteAccount(token: String) : Flow<Boolean>
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

    override suspend fun deleteAccount(token: String): Flow<Boolean> = flow {
        val response = api.deleteAccount(authHeader = token).await()
        when {
            response.success -> emit(true)
            else -> emit(false)
        }
    }.catch {
        emit(false)
    }
}