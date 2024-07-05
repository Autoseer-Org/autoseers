package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingScreen() {
    Scaffold {
        Text(text = "Hello from onboarding", modifier = Modifier.padding(it))
    }
}