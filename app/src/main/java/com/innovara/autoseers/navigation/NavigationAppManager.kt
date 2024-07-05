package com.innovara.autoseers.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.innovara.autoseers.navigation.routes.AutoSeersExperience
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.homeroute.buildHomeScreen
import com.innovara.autoseers.navigation.routes.maps.MapsRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.OnboardingRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.buildOnboardingScreen
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
    modifier: Modifier = Modifier,
    startDestination: OnboardingRoute = OnboardingRoute,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        buildOnboardingScreen()
        navigation<AutoSeersExperience>(startDestination = HomeRoute) {
            buildHomeScreen()
            composable<MapsRoute> { }
            composable<SettingsRoute> { }
        }
    }
}