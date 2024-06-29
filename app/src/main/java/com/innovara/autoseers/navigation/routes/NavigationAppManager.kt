package com.innovara.autoseers.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.navigation.routes.onboardingroute.OnboardingRoute


/**
 * NavigationAppManager serves as the entry point for creating the nav graph for the app
 * Currently the destination type is [Hosted]
 *
 * Uses home routes and onboarding routes to navigate between the 2 pages.
 * A routes describes how to get to a destination. A route can have a payload attached to it
 * TODO(adrian):  once the bottom navigation is implemented we should account for tab navigation
 */
@Composable
fun NavigationAppManager(
    modifier: Modifier = Modifier,
    startDestination: HomeRoute = HomeRoute
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController, startDestination = startDestination) {
        composable<HomeRoute> { backstackEntry ->
            Column {
                Text(text = "Hello from home")
                Button(onClick = { navController.navigate(OnboardingRoute) }) {
                    Text(text = "Navigate to onboarding")
                }
            }
        }
        composable<OnboardingRoute> {
            Column {
                Text(text = "Hello from onboarding")
                Button(onClick = { navController.navigate(HomeRoute) }) {
                    Text(text = "Navigate to home")
                }
            }
        }
    }
}