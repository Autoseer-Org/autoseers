package com.innovara.autoseers.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.homeroute.buildHomeScreen
import com.innovara.autoseers.navigation.routes.maps.MapsRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.OnboardingRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.buildCodeAuthScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildNamePromptScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildOnboardingScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.buildPhoneAuthenticationScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToCodeScreen
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToNamePrompt
import com.innovara.autoseers.navigation.routes.onboardingroute.navigateToPhoneAuthentication
import com.innovara.autoseers.navigation.routes.settings.SettingsRoute


/**
 * NavigationAppManager serves as the entry point for creating the nav graph for the app
 * Currently the destination type is [Hosted]. And has a nested graph to enforce separation of entities and usability
 *
 * Uses home routes and onboarding routes to navigate between the 2 pages.
 * A routes describes how to get to a destination. A route can have a payload attached to it
 * TODO(adrian):  once the bottom navigation is implemented we should account for tab navigation
 */
@Composable
fun NavigationAppManager(
    startDestination: OnboardingRoute = OnboardingRoute,
    navController: NavHostController = rememberNavController(),
    onPhoneNumberEntered: (String) -> Unit,
    onCodeEntered: (String) -> Unit,
    onNameEntered: (String) -> Unit,
    shouldNavigateToCodeScreen: Boolean = false,
    shouldNavigateToNamePrompt: Boolean = false,
) {
    val currentScreen by navController.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController, items = listOf(
                    BottomNavItem(HomeRoute.toString(), Icons.Default.Home, "home"),
                    BottomNavItem(MapsRoute.toString(), Icons.Default.Place, "maps"),
                    BottomNavItem(SettingsRoute.toString(), Icons.Default.Settings, "settings"),
                )
            )
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
            startDestination = startDestination
        ) {
            buildOnboardingScreen(navigateToPhoneAuthentication = {
                navController.navigateToPhoneAuthentication()
            })
            buildPhoneAuthenticationScreen(
                onPhoneNumberEntered = onPhoneNumberEntered,
            )
            buildCodeAuthScreen(
                onCodeEntered = onCodeEntered,
            )

            buildNamePromptScreen(
                onNameEntered = onNameEntered
            )
            navigation<AutoSeersExperience>(startDestination = HomeRoute) {
                buildHomeScreen()
                composable<MapsRoute> { }
                composable<SettingsRoute> { }
            }
        }
    }
}