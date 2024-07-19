package com.innovara.autoseers.navigation.routes.onboardingroute

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
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
    authState: AuthState,
) {
    composable<PhoneAuthenticationRoute> {
        PhoneAuthenticationScreen(
            authState = authState,
            onPhoneNumberEntered = onPhoneNumberEntered,
            onBackPressed = onBackPressed
        )
    }
}

fun NavGraphBuilder.buildCodeAuthScreen(
    onCodeEntered: (String) -> Unit,
) {
    composable<CodeRoute> {
        CodeAuthenticationScreen(onCodeEntered = onCodeEntered)
    }
}

fun NavGraphBuilder.buildNamePromptScreen(
    onNameEntered: (String) -> Unit,
) {
    composable<NamePromptRoute> {
        NamePromptScreen(onNameEntered)
    }
}


// Nav Events
fun NavController.navigateToPhoneAuthentication() = navigate(route = PhoneAuthenticationRoute)
fun NavController.navigateToHomeScreen() = navigate(route = HomeRoute)
fun NavController.navigateToCodeScreen() = navigate(route = CodeRoute)
fun NavController.navigateToNamePrompt() = navigate(route = NamePromptRoute)