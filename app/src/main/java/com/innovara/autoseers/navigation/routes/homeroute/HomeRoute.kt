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
import com.innovara.autoseers.home.RecallsViewModel
import com.innovara.autoseers.home.ui.AlertPage
import com.innovara.autoseers.home.ui.AlertsPage
import com.innovara.autoseers.home.ui.HomeScreen
import com.innovara.autoseers.home.ui.RecallPage
import com.innovara.autoseers.home.ui.RecallsPage
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

@Serializable
object RecallsRoute : GlobalRoute()

@Serializable
data class RecallRoute(
    val nhtsaCampaignNumber: String = "",
    val manufacturer:  String = "",
    val reportReceivedDate: String = "",
    val component: String = "",
    val summary: String = "",
    val consequence: String = "",
    val remedy: String = "",
    val notes: String = "",
    val status: String = ""
) : GlobalRoute()

fun NavGraphBuilder.buildHomeScreen(
    authState: AuthState,
    navigateToAlerts: () -> Unit,
    navigateToRecalls: () -> Unit,
) {
    composable<HomeRoute> {
        if (authState is AuthState.UserAuthenticated) {
            HomeScreen(
                authState,
                navigateToAlerts = navigateToAlerts,
                navigateToRecalls = navigateToRecalls
            )
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
        val shouldShowBookingsButton by alertsViewModel.shouldShowBookingButton.collectAsState()
        AlertPage(
            alertArgument = it.toRoute(),
            navigateBack = onBackPress,
            markAsRepaired = alertsViewModel::markAsRepair,
            onBookAppointment = alertsViewModel::bookAppointment,
            authToken = authState.authAuthenticatedModel.getToken(),
            bookingState = bookingState,
            markAsRepairedState = markAsRepairedState,
            pollingBookingStatusState = pollingBookingStatusState,
            startPollingForBookingStatus = alertsViewModel::pollBookingStatus,
            shouldShowBookingsButton = shouldShowBookingsButton,
        )
    }
}

fun NavGraphBuilder.buildRecallsPage(
    authState: AuthState,
    onBackPress: () -> Unit,
    navigateToRecall: (RecallRoute) -> Unit = {}
) = composable<AlertsRoute> {
    if (authState is AuthState.UserAuthenticated) {
        val recallsViewModel: RecallsViewModel = hiltViewModel()
        LaunchedEffect(key1 = Unit) {
            recallsViewModel.getRecalls(authState.authAuthenticatedModel.getToken())
        }
        val state by recallsViewModel.state.collectAsState()
        RecallsPage(state, onBackPress = onBackPress, navigateToRecall = navigateToRecall)
    }
}

fun NavGraphBuilder.buildRecallPage(
    authState: AuthState,
    onBackPress: () -> Unit
) = composable<AlertRoute> {
    if (authState is AuthState.UserAuthenticated) {
        val recallsViewModel: RecallsViewModel = hiltViewModel()
        val markAsCompleteState by recallsViewModel.markAsCompleteState.collectAsState()
        RecallPage(
            recallArgument = it.toRoute(),
            authToken = authState.authAuthenticatedModel.getToken(),
            markAsCompleteState = markAsCompleteState
        )
    }
}