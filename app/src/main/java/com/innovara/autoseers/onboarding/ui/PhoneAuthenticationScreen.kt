package com.innovara.autoseers.onboarding.ui

import android.annotation.SuppressLint
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthenticationScreen(
    modifier: Modifier = Modifier,
    onPhoneNumberEntered: (String) -> Unit,
    onBackPressed: () -> Unit,
    onPhoneAuthEvents: (OnboardingEvents) -> Unit,
    authState: AuthState,
) {
    var phoneNumber by remember {
        mutableStateOf("")
    }
    val interactionSource = remember { MutableInteractionSource() }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = authState) {
        when (authState) {
            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(authState.message)
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
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Sign up", style = MaterialTheme.typography.headlineLarge)
            Text(text = stringResource(id = R.string.sign_up_long_message))

            TextField(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                },
                label = {
                    Text(text = "Phone number")
                },
                prefix = {
                    Text(text = "+1")
                },
                supportingText = {
                    Text(text = stringResource(id = R.string.support_text_message))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 26.dp),
                onClick = {
                    onPhoneNumberEntered("+1$phoneNumber")
                    focusManager.clearFocus(true)
                    onPhoneAuthEvents.invoke(OnboardingEvents.PhoneNumberEntered)
                }) {
                Text(text = "Continue")
            }
        }
    }
}