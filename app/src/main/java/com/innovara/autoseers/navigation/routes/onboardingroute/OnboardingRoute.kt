package com.innovara.autoseers.navigation.routes.onboardingroute

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.onboarding.initialscreen.ui.InitialScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

fun NavGraphBuilder.buildOnboardingScreen() {
    composable<OnboardingRoute> {
        InitialScreen()
    }
}