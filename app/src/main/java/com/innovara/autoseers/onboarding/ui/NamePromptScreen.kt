package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun NamePromptScreen(
    onNameEntered: (String) -> Unit = {}
) {
    Scaffold {
        var username by remember {
            mutableStateOf("")
        }
        Column(modifier = Modifier.padding(it)) {
            Text(text = "Enter your name")
            TextField(value = username, onValueChange = {
                username = it
            })

            Button(onClick = {
                onNameEntered(username)
            }) {
                Text(text = "Continue")
            }
        }
    }
}