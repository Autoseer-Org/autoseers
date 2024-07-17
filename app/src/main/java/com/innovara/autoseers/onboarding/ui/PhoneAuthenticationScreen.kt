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
fun PhoneAuthenticationScreen(
    modifier: Modifier = Modifier,
    onPhoneNumberEntered: (String) -> Unit = {},
) {
    var phoneNumber by remember {
        mutableStateOf("")
    }
    Scaffold(modifier) {
        Column(modifier = Modifier.padding(it)) {
            Text(text = "Enter phone number")
            TextField(value = phoneNumber, onValueChange = {
                phoneNumber = it
            })
            
            Button(onClick = {
                onPhoneNumberEntered(phoneNumber)
            }) {
                Text(text = "Continue")
            }
        }
    }
}