package com.innovara.autoseers.onboarding.logic

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.di.analytics.onboardingevents.OnboardingAnalyticsEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class OnboardingEvents {
    data object PhoneNumberEntered : OnboardingEvents()
    data object CodeEntered : OnboardingEvents()
    data object NameEntered : OnboardingEvents()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingAnalyticsEvents: OnboardingAnalyticsEvents,
) : ViewModel() {
    fun handleAnalyticsEvents(): (OnboardingEvents) -> Unit {
        return {
            when (it) {
                OnboardingEvents.PhoneNumberEntered -> onboardingAnalyticsEvents.logPhoneNumberEntered()
                OnboardingEvents.CodeEntered -> onboardingAnalyticsEvents.logCodeNumberEntered()
                OnboardingEvents.NameEntered -> onboardingAnalyticsEvents.logNameEntered("")
            }
        }
    }
}