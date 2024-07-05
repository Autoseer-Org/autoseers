package com.innovara.autoseers.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen() {
    Scaffold {
        Text(text = "Hello from home screen", modifier = Modifier.padding(it))
    }
}