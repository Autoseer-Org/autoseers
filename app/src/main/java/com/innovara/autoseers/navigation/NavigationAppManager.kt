package com.innovara.autoseers.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.homeroute.AlertRoute
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.homeroute.buildAlertsPage
import com.innovara.autoseers.navigation.routes.homeroute.buildHomeScreen
import com.innovara.autoseers.navigation.routes.maps.MapsRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.OnboardingRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.buildCodeAuthScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildNamePromptScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildOnboardingScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildPhoneAuthenticationScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToCodeScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToAutoSeersExperience
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToNamePrompt
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToPhoneAuthentication
import com.innovara.autoseers.navigation.routes.settings.SettingsRoute
import com.innovara.autoseers.navigation.routes.settings.buildSettingsScreen
import com.innovara.autoseers.onboarding.logic.OnboardingState
import com.innovara.autoseers.onboarding.logic.OnboardingViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


/**
 * NavigationAppManager serves as the entry point for creating the nav graph for the app
 * Currently the destination type is [Hosted]. And has a nested graph to enforce separation of entities and usability
 *
 * Uses home routes and onboarding routes to navigate between the 2 pages.
 * A routes describes how to get to a destination. A route can have a payload attached to it
 */
@Composable
fun NavigationAppManager(
    navController: NavHostController = rememberNavController(),
    onPhoneNumberEntered: (String) -> Unit = {},
    onCodeEntered: (String) -> Unit = {},
    authState: AuthState,
    resetAuthState: () -> Unit = {},
    onLogoutPressed: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val onboardingViewModel = hiltViewModel<OnboardingViewModel>()
    val onboardingState by onboardingViewModel.state.collectAsState()
    val analyticsEvents = onboardingViewModel.handleAnalyticsEvents()

    var shouldShowBottomNavBar by remember {
        mutableStateOf(false)
    }

    val shouldNavigateToCodeScreen = remember(authState) {
        when (authState) {
            is AuthState.PhoneVerificationInProgress ->
                authState.authInProgressModel.shouldTransitionToCodeScreen

            else -> false
        }
    }

    val shouldNavigateToNamePrompt = remember(authState, shouldShowBottomNavBar) {
        when (authState) {
            is AuthState.UserAuthenticated -> {
                if (authState.shouldSkipNameStep) {
                    navController.navigateToAutoSeersExperience()
                    shouldShowBottomNavBar = true
                    false
                } else {
                    !shouldShowBottomNavBar
                }
            }

            else -> false
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNavBar) {
                BottomNavBar(
                    navController = navController, items = listOf(
                        BottomNavItem(HomeRoute, Icons.Default.Home, "home"),
                        BottomNavItem(MapsRoute, Icons.Default.Place, "maps"),
                        BottomNavItem(SettingsRoute, Icons.Default.Settings, "settings"),
                    )
                )
            }
        }
    ) {
        if (shouldNavigateToCodeScreen) {
            navController.navigateToCodeScreen()
        }

        if (shouldNavigateToNamePrompt) {
            navController.navigateToNamePrompt()
        }
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = if (authState is AuthState.UserAuthenticated && authState.shouldSkipNameStep) AutoSeersExperience else OnboardingRoute
        ) {
            buildOnboardingScreen(navigateToPhoneAuthentication = {
                navController.navigateToPhoneAuthentication()
            })
            buildPhoneAuthenticationScreen(
                authState = authState,
                onPhoneNumberEntered = onPhoneNumberEntered,
                onBackPressed = {
                    navController.popBackStack()
                    resetAuthState()
                },
                onPhoneAuthEvents = analyticsEvents,
            )
            buildCodeAuthScreen(
                authState = authState,
                onCodeEntered = onCodeEntered,
                onBackPressed = {
                    navController.popBackStack()
                    resetAuthState()
                },
                onCodeAuthEvents = analyticsEvents,
            )

            buildNamePromptScreen(
                onNameEntered = { name ->
                    scope.launch(CoroutineExceptionHandler { _, error ->
                        Log.e("ERROR", error.message ?: "")
                    }) {
                        if (authState is AuthState.UserAuthenticated) {
                            onboardingViewModel.sendOnboardingData(
                                tokenId = authState.authAuthenticatedModel.tokenId,
                                username = name
                            )
                        }
                    }.invokeOnCompletion {
                        if (onboardingState is OnboardingState.NewUserCreated && authState is AuthState.UserAuthenticated) {
                            shouldShowBottomNavBar = true
                            navController.navigateToAutoSeersExperience()
                        }
                    }
                },
                onNameEnteredEvents = analyticsEvents,
            )
            navigation<AutoSeersExperience>(startDestination = HomeRoute) {
                buildHomeScreen(authState) {
                    navController.navigate(AlertRoute)
                }
                buildAlertsPage(authState) {
                    navController.popBackStack()
                }
                composable<MapsRoute> { }
                buildSettingsScreen(authState, onLogoutPress = {
                    onLogoutPressed()
                    resetAuthState()
                    shouldShowBottomNavBar = false
                })
            }
        }
    }
}