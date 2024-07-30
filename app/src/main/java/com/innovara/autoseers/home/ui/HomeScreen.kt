package com.innovara.autoseers.home.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.home.HomeFileProvider

import com.innovara.autoseers.R
import com.innovara.autoseers.home.HomeState
import com.innovara.autoseers.home.HomeViewModel
import com.innovara.autoseers.home.UploadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authState: AuthState.UserAuthenticated,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    onFailToUploadImage: () -> Unit = {},
) {
    var uri by remember {
        mutableStateOf(Uri.parse(""))
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val uploadState by homeViewModel.uploadState.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.getHomeData(authState.authAuthenticatedModel.tokenId)
    }

    LaunchedEffect(key1 = uploadState) {
        if (uploadState is UploadState.Failed) {
            snackbarHostState.showSnackbar(
                message = "Failed to upload image. Try again",
                duration = SnackbarDuration.Long
            )
        }
        if (uploadState is UploadState.Success) {
            homeViewModel.getHomeData(authState.authAuthenticatedModel.tokenId)
        }
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
                } catch (e: Exception) {
                    Log.e("Failed to upload", e.localizedMessage ?: "")
                    onFailToUploadImage()
                }
            }
        }
    val state by homeViewModel.homeState.collectAsState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    uri = HomeFileProvider.getImageUri(context = context)
                    carReportPicker.launch(uri)
                }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        },
        topBar = {
            if (state is HomeState.Loaded) {
                TopAppBar(title = {
                    Text(text = (state as HomeState.Loaded).homeModel.carModelMake)
                })
            }
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            when (val homeState = state) {
                is HomeState.Empty -> EmptyHomeUi(uploadState = uploadState)
                is HomeState.Loading -> LoadingHomeUi()
                is HomeState.Loaded -> LoadedHomeUi(homeState.homeModel)
            }
        }
    }
}

@Composable
fun EmptyHomeUi(modifier: Modifier = Modifier, uploadState: UploadState) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.animation2))
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (uploadState is UploadState.Uploading) {
                LottieAnimation(
                    modifier = Modifier.size(200.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                AnimatedText()
            } else {
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
}

@Composable
fun AnimatedText() {
    val stateFlow = remember {
        AnimatedTextValues.createStateFlow()
    }
    val state by stateFlow. collectAsState(initial = AnimatedTextValues.FIRST_TEXT)
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(3000)) togetherWith fadeOut(animationSpec = tween(1000))
        }, label = "animation"
    ) { targetState ->
        when (targetState) {
            AnimatedTextValues.FIRST_TEXT -> Text(text = targetState.value)
            AnimatedTextValues.SECOND_TEXT -> Text(text = targetState.value)
            AnimatedTextValues.THIRD_TEXT -> Text(text = targetState.value)
        }
    }
}

enum class AnimatedTextValues(val value: String) {
    FIRST_TEXT("Hang tight! We're processing your file. Do not leave this page"),
    SECOND_TEXT("Gemini is wrapping things up."),
    THIRD_TEXT("Finalizing your request. Almost there!");

    companion object {
        fun createStateFlow(): Flow<AnimatedTextValues> = flow {
            entries.forEach {
                emit(it)
                delay(5000L)
            }
        }
    }
}