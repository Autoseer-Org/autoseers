package com.innovara.autoseers.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.homeroute.buildAlertPage
import com.innovara.autoseers.navigation.routes.homeroute.buildAlertsPage
import com.innovara.autoseers.navigation.routes.homeroute.buildHomeScreen
import com.innovara.autoseers.navigation.routes.recommendations.RecommendationsRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.OnboardingRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.buildCodeAuthScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildNamePromptScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildOnboardingScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildPhoneAuthenticationScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToCodeScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToNamePrompt
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToPhoneAuthentication
import com.innovara.autoseers.navigation.routes.settings.SettingsRoute
import com.innovara.autoseers.navigation.routes.settings.buildSettingsScreen
import com.innovara.autoseers.onboarding.logic.OnboardingState
import com.innovara.autoseers.onboarding.logic.OnboardingViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import com.innovara.autoseers.R
import com.innovara.autoseers.navigation.routes.homeroute.navigateToAlerts
import com.innovara.autoseers.navigation.routes.homeroute.navigateToAutoSeersExperience
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToOnboardingScreen
import com.innovara.autoseers.navigation.routes.recommendations.buildRecommendedServices
import com.innovara.autoseers.navigation.routes.settings.buildThemePage
import com.innovara.autoseers.navigation.routes.settings.navigateToThemePage

/**
 * NavigationAppManager serves as the entry point for creating the nav graph for the app
 * Currently the destination type is [Hosted]. And has a nested graph to enforce separation of entities and usability
 *
 * Uses multiple routes to navigate between pages.
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

    var shouldShowBottomNavBar by rememberSaveable {
        mutableStateOf(false)
    }

    val shouldNavigateToCodeScreen = rememberSaveable(authState) {
        when (authState) {
            is AuthState.PhoneVerificationInProgress ->
                authState.authInProgressModel.shouldTransitionToCodeScreen

            else -> false
        }
    }

    val shouldNavigateToNamePrompt = rememberSaveable(authState) {
        when (authState) {
            is AuthState.UserAuthenticated -> !authState.shouldSkipNameStep
            else -> false
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isMainRoute by remember {
        derivedStateOf {
            navBackStackEntry?.isAllowedToSeeBottomNavBar() == true
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNavBar && isMainRoute) {
                BottomNavBar(
                    navController = navController,
                    items = listOf(
                        BottomNavItem(
                            HomeRoute,
                            ImageVector.vectorResource(id = R.drawable.outline_home_24),
                            ImageVector.vectorResource(id = R.drawable.baseline_home_24),
                            "Home",
                        ),
                        BottomNavItem(
                            RecommendationsRoute,
                            ImageVector.vectorResource(id = R.drawable.outline_bolt_24),
                            ImageVector.vectorResource(id = R.drawable.baseline_bolt_24),
                            "Services"
                        ),
                        BottomNavItem(
                            SettingsRoute,
                            ImageVector.vectorResource(id = R.drawable.outline_settings_24),
                            ImageVector.vectorResource(id = R.drawable.baseline_settings_24),
                            "Settings"
                        ),
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

        LaunchedEffect(authState, onboardingState) {
            if ((authState is AuthState.UserAuthenticated && authState.shouldSkipNameStep) || onboardingState is OnboardingState.NewUserCreated) {
                navController.navigateToAutoSeersExperience()
                shouldShowBottomNavBar = true
            }
        }
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = OnboardingRoute,
        ) {
            buildOnboardingScreen(
                authState = authState,
                navigateToPhoneAuthentication = { isSignUpFlow ->
                    navController.navigateToPhoneAuthentication(isSignUpFlow)
                })
            buildPhoneAuthenticationScreen(
                authState = authState,
                onPhoneNumberEntered = onPhoneNumberEntered,
                onBackPressed = {
                    navController.popBackStack()
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
                                tokenId = authState.authAuthenticatedModel.getToken(),
                                username = name
                            )
                        }
                    }
                },
                onNameEnteredEvents = analyticsEvents,
            )
            navigation<AutoSeersExperience>(startDestination = HomeRoute) {
                buildHomeScreen(authState) {
                    navController.navigateToAlerts()
                }
                buildAlertsPage(
                    authState,
                    onBackPress = { navController.popBackStack() }) { alertRoute ->
                    navController.navigate(alertRoute)
                }
                buildAlertPage(authState) {
                    navController.popBackStack()
                }
                buildRecommendedServices(authState)
                buildSettingsScreen(authState,
                    deleteAccount = {
                        if (authState is AuthState.UserAuthenticated) {
                            scope.launch {
                                onboardingViewModel
                                    .deleteAccount(
                                        tokenId = authState.authAuthenticatedModel.getToken()
                                    )
                            }.invokeOnCompletion {
                                prepareUiForLoggedOutState(
                                    onLogoutPressed,
                                    resetAuthState
                                ) { bottomNavBarValue ->
                                    shouldShowBottomNavBar = bottomNavBarValue
                                }.also {
                                    navController.navigateToOnboardingScreen()
                                }
                            }
                        }
                    },
                    navigateToThemePage = {
                        navController.navigateToThemePage()
                    }, onLogoutPress = {
                        prepareUiForLoggedOutState(
                            onLogoutPressed,
                            resetAuthState
                        ) { bottomNavBarValue ->
                            shouldShowBottomNavBar = bottomNavBarValue
                        }.also {
                            navController.navigateToOnboardingScreen()
                        }
                    })
                buildThemePage(onBackPress = {
                    navController.popBackStack()
                })
            }
        }
    }
}

private fun prepareUiForLoggedOutState(
    onLogoutPressed: () -> Unit,
    resetAuthState: () -> Unit,
    onBottomNavBarChanged: (Boolean) -> Unit
) {
    onLogoutPressed()
    resetAuthState()
    onBottomNavBarChanged(false)
}