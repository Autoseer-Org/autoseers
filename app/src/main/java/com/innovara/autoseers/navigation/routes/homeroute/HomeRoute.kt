package com.innovara.autoseers.navigation.routes.homeroute

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.AlertsViewModel
import com.innovara.autoseers.home.ui.AlertPage
import com.innovara.autoseers.home.ui.AlertsPage
import com.innovara.autoseers.home.ui.HomeScreen
import com.innovara.autoseers.navigation.routes.GlobalRoute
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute : GlobalRoute() {
    override fun toString(): String = "HomeRoute"
}

@Serializable
object AlertsRoute : GlobalRoute()

@Serializable
data class AlertRoute(
    val alertName: String = "",
    val alertDescription: String = "",
    val alertCategory: String = "",
    val alertState: String = "",
    val alertId: String = ""
) : GlobalRoute()

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
    navigateToAlert: (AlertRoute) -> Unit = {}
) = composable<AlertsRoute> {
    if (authState is AuthState.UserAuthenticated) {
        val alertsViewModel: AlertsViewModel = hiltViewModel()
        LaunchedEffect(key1 = Unit) {
            alertsViewModel.getAlerts(authState.authAuthenticatedModel.getToken())
        }
        val state by alertsViewModel.state.collectAsState()
        AlertsPage(state, onBackPress = onBackPress, navigateToAlert = navigateToAlert)
    }
}

fun NavGraphBuilder.buildAlertPage(
    authState: AuthState,
    onBackPress: () -> Unit
) = composable<AlertRoute> {
    if (authState is AuthState.UserAuthenticated) {
        val alertsViewModel: AlertsViewModel = hiltViewModel()
        val bookingState by alertsViewModel.bookingState.collectAsState()
        val markAsRepairedState by alertsViewModel.repairedState.collectAsState()
        val pollingBookingStatusState by alertsViewModel.pollingBookingStatusState.collectAsState()
        AlertPage(
            alertArgument = it.toRoute(),
            navigateBack = onBackPress,
            markAsRepaired = alertsViewModel::markAsRepair,
            onBookAppointment = alertsViewModel::bookAppointment,
            authToken = authState.authAuthenticatedModel.getToken(),
            bookingState = bookingState,
            markAsRepairedState = markAsRepairedState,
            pollingBookingStatusState = pollingBookingStatusState,
            startPollingForBookingStatus = alertsViewModel::pollBookingStatus
        )
    }
}