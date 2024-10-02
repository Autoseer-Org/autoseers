package com.innovara.autoseers.navigation.routes.settings

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.GlobalRoute
import com.innovara.autoseers.settings.ui.SettingsPage
import com.innovara.autoseers.settings.ui.SettingsViewModel
import com.innovara.autoseers.settings.ui.ThemePage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : GlobalRoute() {
    override fun toString(): String = "SettingsRoute"
}

@Serializable
data object ThemeRoute : GlobalRoute() {
    override fun toString(): String = "ThemePage"
}

fun NavGraphBuilder.buildSettingsScreen(
    authState: AuthState,
    onLogoutPress: () -> Unit = {},
    navigateToThemePage: () -> Unit = {},
    navigateToNotifications: () -> Unit = {},
) {
    composable<SettingsRoute> {
        if (authState is AuthState.UserAuthenticated) {
            SettingsPage(
                onLogoutPress = onLogoutPress,
                navigateToThemePage = navigateToThemePage,
                navigateToNotifications = navigateToNotifications
            )
        }
    }
}

fun NavGraphBuilder.buildThemePage(
    onBackPress: () -> Unit = {},
) = composable<ThemeRoute> {
    val viewModel: SettingsViewModel = hiltViewModel()
    val currentTheme by viewModel.isDarkThemeSelected.collectAsState()
    val scope = rememberCoroutineScope()
    ThemePage(onThemeChanged = { newValue ->
        scope.launch {
            viewModel.changeTheme(newValue)
        }
    }, onBackPress = onBackPress, currentTheme)
}

fun NavController.navigateToThemePage() = navigate(ThemeRoute)