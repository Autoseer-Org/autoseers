package com.innovara.autoseers.navigation.routes.homeroute

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.AlertsViewModel
import com.innovara.autoseers.home.HomeViewModel
import com.innovara.autoseers.home.ui.AlertsPage
import com.innovara.autoseers.home.ui.HomeScreen
import com.innovara.autoseers.navigation.routes.GlobalRoute
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute : GlobalRoute() {
    override fun toString(): String = "HomeRoute"
}

@Serializable
object AlertRoute : GlobalRoute()

fun NavGraphBuilder.buildHomeScreen(
    authState: AuthState,
    navigateToAlerts: () -> Unit,
) {
    composable<HomeRoute> {
        if (authState is AuthState.UserAuthenticated) {
            HomeScreen(authState, navigateToAlerts = navigateToAlerts)
        }
    }
}

fun NavGraphBuilder.buildAlertsPage(
    authState: AuthState,
    onBackPress: () -> Unit,
) = composable<AlertRoute> {
    if (authState is AuthState.UserAuthenticated) {
        val alertsViewModel: AlertsViewModel = hiltViewModel()
        LaunchedEffect(key1 = Unit) {
            alertsViewModel.getAlerts(authState.authAuthenticatedModel.tokenId)
        }
        val state by alertsViewModel.state.collectAsState()
        AlertsPage(state, onBackPress = onBackPress)
    }
}