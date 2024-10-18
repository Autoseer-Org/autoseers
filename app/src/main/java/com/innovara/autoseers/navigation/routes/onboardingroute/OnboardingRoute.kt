package com.innovara.autoseers.navigation.routes.onboardingroute

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.GlobalRoute
import com.innovara.autoseers.onboarding.logic.OnboardingEvents
import com.innovara.autoseers.onboarding.ui.CodeAuthenticationScreen
import com.innovara.autoseers.onboarding.ui.InitialScreen
import com.innovara.autoseers.onboarding.ui.NamePromptScreen
import com.innovara.autoseers.onboarding.ui.PhoneAuthenticationScreen
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute: GlobalRoute() {
    override fun toString(): String = "OnboardingRoute"
}

@Serializable
data class PhoneAuthenticationRoute(
    val isSignUpFlow: Boolean
)

@Serializable
object CodeRoute

@Serializable
object NamePromptRoute

fun NavGraphBuilder.buildOnboardingScreen(
    navigateToPhoneAuthentication: (Boolean) -> Unit = {},
    authState: AuthState,
) {
    composable<OnboardingRoute> {
        if (authState is AuthState.NotAuthenticated) {
            InitialScreen(
                onStartPressed = navigateToPhoneAuthentication
            )
        }else {
            Unit
        }
    }
}

fun NavGraphBuilder.buildPhoneAuthenticationScreen(
    onPhoneNumberEntered: (String) -> Unit,
    onBackPressed: () -> Unit,
    onPhoneAuthEvents: (OnboardingEvents) -> Unit,
    authState: AuthState,
) {
    composable<PhoneAuthenticationRoute> {
        val phoneRoute = it.toRoute<PhoneAuthenticationRoute>()
        PhoneAuthenticationScreen(
            authState = authState,
            onPhoneNumberEntered = onPhoneNumberEntered,
            onBackPressed = onBackPressed,
            onPhoneAuthEvents = onPhoneAuthEvents,
            isSignUpFlow = phoneRoute.isSignUpFlow
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
fun NavController.navigateToPhoneAuthentication(isSignUpFlow: Boolean) = navigate(route = PhoneAuthenticationRoute(isSignUpFlow))

fun NavController.navigateToCodeScreen() = navigate(route = CodeRoute)
fun NavController.navigateToNamePrompt() = navigate(route = NamePromptRoute)
fun NavController.navigateToOnboardingScreen() {
    navigate(OnboardingRoute) {
        popUpTo(AutoSeersExperience){
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
    }
}