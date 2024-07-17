package com.innovara.autoseers.navigation.routes.onboardingroute

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.onboarding.ui.InitialScreen
import com.innovara.autoseers.onboarding.ui.PhoneAuthenticationScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute
@Serializable
object PhoneAuthenticationRoute

fun NavGraphBuilder.buildOnboardingScreen(
    navigateToPhoneAuthentication: () -> Unit = {},
) {
    composable<OnboardingRoute> {
        InitialScreen(
            onStartPressed = navigateToPhoneAuthentication
        )
    }
}

fun NavGraphBuilder.buildPhoneAuthenticationScreen(
    onPhoneNumberEntered: (String) -> Unit = {},
) {
    composable<PhoneAuthenticationRoute> {
        PhoneAuthenticationScreen(
            onPhoneNumberEntered = onPhoneNumberEntered
        )
    }
}


// Nav Events
fun NavController.navigateToPhoneAuthentication() {
    navigate(route = PhoneAuthenticationRoute)
}

fun NavController.navigateToHomeScreen() {
    navigate(route = HomeRoute)
}