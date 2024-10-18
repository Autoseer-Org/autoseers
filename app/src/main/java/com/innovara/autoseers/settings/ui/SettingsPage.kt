package com.innovara.autoseers.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    navigateToThemePage: () -> Unit,
    navigateToNotifications: () -> Unit,
    onLogoutPress: () -> Unit,
    deleteAccount: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    if (showDeleteDialog) {
        DeleteAccountDialog(onDismiss = { showDeleteDialog = !showDeleteDialog }) {
            deleteAccount()
            showDeleteDialog = !showDeleteDialog
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Settings") })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SettingsRow(title = "Notifications") {
                navigateToNotifications()
            }
            SettingsRow(title = "Theme") {
                navigateToThemePage()
            }
            SettingsRow(title = "Improve the app") {
                uriHandler.openUri("https://forms.gle/6jSR2ENSmiUwfUMv7")
            }
            TextButton(onClick = onLogoutPress, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Log out")
            }
            TextButton(onClick = {
                showDeleteDialog = !showDeleteDialog
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Delete account", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun SettingsRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = true, onClick = onClick)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(12.dp)
            .fillMaxWidth(),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Icon(painter = painterResource(id = R.drawable.settings_arrow), contentDescription = "")
    }
}

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
        Button(onClick = onConfirm) {
            Text(text = "Delete")
        }
    },
    dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = "Cancel")
        }
    },
    title = {
        Text(text = "Delete account")
    }, text = {
        Text(
            text = "Are you sure you want to delete your account? This action is Irreversible and will immediately delete all your data!",
            style = MaterialTheme.typography.bodySmall
        )
    })

@Preview()
@Composable
fun SettingsRowPreview() {
    SettingsRow("Improve the app") {}
}

@Preview()
@Composable
fun DeleteAccountDialogPreview() {
    DeleteAccountDialog(onDismiss = {}, onConfirm = {})
}