package com.innovara.autoseers.home.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.HomeFileProvider

import com.innovara.autoseers.R
import com.innovara.autoseers.home.HomeState
import com.innovara.autoseers.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    authState: AuthState.UserAuthenticated,
    onFailToUploadImage: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var uri by remember {
        mutableStateOf(Uri.parse(""))
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        homeViewModel.getHomeData(authState.authAuthenticatedModel.tokenId)
    }

    val carReportPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { wasPictureSaved ->
            if (wasPictureSaved) {
                Log.i("PICTURE", uri.normalizeScheme().scheme ?: "")
                try {
                    val stream = context.contentResolver.openInputStream(uri)
                    scope.launch {
                        homeViewModel.uploadImageReport(
                            authState.authAuthenticatedModel.tokenId,
                            stream?.readBytes() ?: ByteArray(0)
                        )
                    }.invokeOnCompletion {
                        stream?.close()
                    }
                }catch (e: Exception) {
                    Log.e("Failed to upload", e.localizedMessage ?: "")
                    onFailToUploadImage()
                }
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
        Surface(modifier = Modifier.padding(it)) {
            val state by homeViewModel.homeState.collectAsState()
            when (state) {
                is HomeState.Empty -> EmptyHomeUi()
                is HomeState.Loading -> LoadingHomeUi()
                else -> Text(text = "Loaded")
            }
        }
    }
}

@Composable
fun LoadingHomeUi(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyHomeUi(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            Image(
                modifier = Modifier.size(260.dp),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.modern_car), contentDescription = ""
            )
            Text(
                text = "You can upload your multi-checkpoint\n" +
                        "report by pressing on the plus sign"
            )
        }
    }
}