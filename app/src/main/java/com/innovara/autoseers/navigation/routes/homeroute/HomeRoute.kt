package com.innovara.autoseers.navigation.routes.homeroute

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.home.ui.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.buildHomeScreen() {
    composable<HomeRoute> {
        HomeScreen()
    }
}