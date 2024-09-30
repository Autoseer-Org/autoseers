package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.R
import com.innovara.autoseers.onboarding.logic.OnboardingEvents

@Composable
fun NamePromptScreen(
    onNameEntered: (String) -> Unit = {},
    onNameEnteredEvents: (OnboardingEvents) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Scaffold {
        var username by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.name_title_screen),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(text = stringResource(id = R.string.name_description_screen))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = username, onValueChange = { newName ->
                username = newName
            },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus(true)
                })
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 26.dp),
                onClick = {
                    onNameEntered(username)
                    focusManager.clearFocus(true)
                    onNameEnteredEvents.invoke(OnboardingEvents.NameEntered)
                }) {
                Text(text = "Finish setup")
            }
        }
    }
}