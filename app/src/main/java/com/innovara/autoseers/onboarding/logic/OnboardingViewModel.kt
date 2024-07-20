package com.innovara.autoseers.onboarding.logic

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.api.onboarding.OnboardingRequest
import com.innovara.autoseers.api.onboarding.OnboardingService
import com.innovara.autoseers.api.onboarding.OnboardingServiceState
import com.innovara.autoseers.di.analytics.onboardingevents.OnboardingAnalyticsEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class OnboardingEvents {
    data object PhoneNumberEntered : OnboardingEvents()
    data object CodeEntered : OnboardingEvents()
    data object NameEntered : OnboardingEvents()
}

sealed class OnboardingState {
    data object Idle : OnboardingState()
    data class FailedToCreateNewUser(
        val reason: String
    ) : OnboardingState()

    data object NewUserCreated : OnboardingState()
    data object Loading : OnboardingState()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingAnalyticsEvents: OnboardingAnalyticsEvents,
    private val onboardingService: OnboardingService,
) : ViewModel() {

    private val _state: MutableStateFlow<OnboardingState> = MutableStateFlow(OnboardingState.Idle)
    val state: StateFlow<OnboardingState> = _state

    fun handleAnalyticsEvents(): (OnboardingEvents) -> Unit {
        return {
            when (it) {
                OnboardingEvents.PhoneNumberEntered -> onboardingAnalyticsEvents.logPhoneNumberEntered()
                OnboardingEvents.CodeEntered -> onboardingAnalyticsEvents.logCodeNumberEntered()
                OnboardingEvents.NameEntered -> onboardingAnalyticsEvents.logNameEntered("")
            }
        }
    }

    suspend fun sendOnboardingData(tokenId: String, username: String) {
        onboardingService.sendOnboardingData(
            OnboardingRequest(
                tokenId = tokenId,
                userName = username
            )
        ).collectLatest(::updateState)
    }

    private fun updateState(onboardingState: OnboardingServiceState) {
        when (onboardingState) {
            is OnboardingServiceState.OnboardingSuccess -> _state.update {
                OnboardingState.NewUserCreated
            }

            is OnboardingServiceState.OnboardingFailed -> _state.update {
                OnboardingState.FailedToCreateNewUser(
                    reason = onboardingState.reason
                )
            }
        }
    }
}