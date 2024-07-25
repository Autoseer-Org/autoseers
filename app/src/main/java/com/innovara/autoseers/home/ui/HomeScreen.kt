package com.innovara.autoseers.home.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import com.innovara.autoseers.home.HomeFileProvider

@Composable
fun HomeScreen() {
    var uri by remember {
        mutableStateOf(Uri.parse(""))
    }
    val context = LocalContext.current
    val carReportPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { wasPictureSaved ->
            if (wasPictureSaved) {
                Log.i("PICTURE", uri.normalizeScheme().scheme ?: "")
            }
        }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    // start upload process
                    uri = HomeFileProvider.getImageUri(context = context)
                    carReportPicker.launch(uri)
                }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }
    ) {
        Text(text = "Hello from home screen", modifier = Modifier.padding(it))
    }
}