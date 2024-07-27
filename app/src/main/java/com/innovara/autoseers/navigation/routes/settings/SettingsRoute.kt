package com.innovara.autoseers.navigation.routes.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.ui.HomeScreen
import com.innovara.autoseers.navigation.routes.GlobalRoute
import com.innovara.autoseers.navigation.routes.homeroute.HomeRoute
import com.innovara.autoseers.settings.ui.SettingsPage
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute: GlobalRoute() {
    override fun toString(): String = "SettingsRoute"
}

fun NavGraphBuilder.buildSettingsScreen(
    authState: AuthState,
    onLogoutPress: () -> Unit = {}
) {
    composable<SettingsRoute> {
        if (authState is AuthState.UserAuthenticated) {
            SettingsPage(authState, onLogoutPress = onLogoutPress)
        }
    }
}