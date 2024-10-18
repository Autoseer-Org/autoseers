package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.R
import com.innovara.autoseers.onboarding.logic.OnboardingEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeAuthenticationScreen(
    authState: AuthState,
    modifier: Modifier = Modifier,
    onCodeEntered: (String) -> Unit = {},
    onBackPressed: () -> Unit = {},
    onCodeAuthEvents: (OnboardingEvents) -> Unit = {},
) {
    var code by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = authState) {
        when (authState) {
            is AuthState.PhoneVerificationInProgress -> {
                if (authState.error) {
                    snackbarHostState.showSnackbar(authState.errorMessage ?: "")
                }
            }

            else -> Unit
        }
    }
    Scaffold(
        modifier,
        topBar = {
            TopAppBar(title = { }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) { paddingValues ->
        val style = MaterialTheme.typography.headlineLarge
        var headerTextStyle by remember {
            mutableStateOf(style)
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.code_sent_title),
                style = headerTextStyle,
                softWrap = false,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        headerTextStyle =
                            headerTextStyle.copy(fontSize = headerTextStyle.fontSize * .9)
                    }
                }
            )
            Text(
                text = stringResource(id = R.string.code_sent_message),
                style = MaterialTheme.typography.bodySmall
            )

            TextField(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                value = code,
                onValueChange = { newCode ->
                    code = newCode
                },
                label = {
                    Text(text = "Code")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                    showKeyboardOnFocus = true,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        this.defaultKeyboardAction(imeAction = ImeAction.Done)

                    }
                ),
                interactionSource = interactionSource,
            )

            Button(
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 26.dp),
                onClick = {
                    isLoading = true
                    onCodeEntered(code)
                    focusManager.clearFocus(true)
                    onCodeAuthEvents.invoke(OnboardingEvents.CodeEntered)
                }) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = stringResource(id = R.string.next_button))
                }
            }
        }
    }
}