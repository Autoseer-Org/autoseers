package com.innovara.autoseers.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.innovara.autoseers.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    authState: AuthState.UserAuthenticated,
    onLogoutPress: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Settings")})
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Button(onClick = onLogoutPress) {
                Text(text = "Log out")
            }
        }
    }
}