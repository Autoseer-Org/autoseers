package com.innovara.autoseers.navigation.routes.onboardingroute

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.onboarding.logic.OnboardingEvents
import com.innovara.autoseers.onboarding.ui.CodeAuthenticationScreen
import com.innovara.autoseers.onboarding.ui.InitialScreen
import com.innovara.autoseers.onboarding.ui.NamePromptScreen
import com.innovara.autoseers.onboarding.ui.PhoneAuthenticationScreen
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

@Serializable
object PhoneAuthenticationRoute

@Serializable
object CodeRoute

@Serializable
object NamePromptRoute

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
    onPhoneNumberEntered: (String) -> Unit,
    onBackPressed: () -> Unit,
    onPhoneAuthEvents: (OnboardingEvents) -> Unit,
    authState: AuthState,
) {
    composable<PhoneAuthenticationRoute> {
        PhoneAuthenticationScreen(
            authState = authState,
            onPhoneNumberEntered = onPhoneNumberEntered,
            onBackPressed = onBackPressed,
            onPhoneAuthEvents = onPhoneAuthEvents
        )
    }
}

fun NavGraphBuilder.buildCodeAuthScreen(
    authState: AuthState,
    onCodeEntered: (String) -> Unit,
    onBackPressed: () -> Unit,
    onCodeAuthEvents: (OnboardingEvents) -> Unit,
) {
    composable<CodeRoute> {
        CodeAuthenticationScreen(
            authState = authState,
            onCodeEntered = onCodeEntered,
            onBackPressed = onBackPressed,
            onCodeAuthEvents = onCodeAuthEvents,
        )
    }
}

fun NavGraphBuilder.buildNamePromptScreen(
    onNameEntered: (String) -> Unit,
    onNameEnteredEvents: (OnboardingEvents) -> Unit,
) {
    composable<NamePromptRoute> {
        NamePromptScreen(onNameEntered, onNameEnteredEvents)
    }
}


// Nav Events
fun NavController.navigateToPhoneAuthentication() = navigate(route = PhoneAuthenticationRoute)
fun NavController.navigateToAutoSeersExperience() = navigate(route = AutoSeersExperience) {
    popBackStack(route = HomeRoute, inclusive = false) // Assumes HomeRoute is the root of the nested graph
}

fun NavController.navigateToCodeScreen() = navigate(route = CodeRoute)
fun NavController.navigateToNamePrompt() = navigate(route = NamePromptRoute)