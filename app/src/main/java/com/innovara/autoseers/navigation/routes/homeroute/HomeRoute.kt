package com.innovara.autoseers.navigation.routes.homeroute

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.HomeViewModel
import com.innovara.autoseers.home.ui.HomeScreen
import com.innovara.autoseers.navigation.routes.GlobalRoute
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute: GlobalRoute() {
    override fun toString(): String = "HomeRoute"
}

fun NavGraphBuilder.buildHomeScreen(
    authState: AuthState,
) {
    composable<HomeRoute> {
        if (authState is AuthState.UserAuthenticated) {
            HomeScreen(authState)
        }
    }
}