package com.innovara.autoseers.di.analytics.onboardingevents

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.innovara.autoseers.di.firebase.FirebaseService
import javax.inject.Inject


interface OnboardingAnalyticsEvents {
    fun logPhoneNumberEntered()
    fun logCodeNumberEntered()
    fun logNameEntered(name: String)
    fun logUserCreated()
}

class OnboardingAnalyticsEventsImpl @Inject constructor(
    firebaseService: FirebaseService,
) : OnboardingAnalyticsEvents {
    private val analytics = firebaseService.analytics()
    private fun FirebaseAnalytics.logEvent(event: String) =
        logEvent(event, Bundle())
    override fun logPhoneNumberEntered() = analytics
        .logEvent("add_phone_number")

    override fun logCodeNumberEntered() = analytics
        .logEvent("add_code_number")

    override fun logNameEntered(name: String) = analytics
        .logEvent("add_user_name")

    override fun logUserCreated() = analytics
        .logEvent("user_created")
}