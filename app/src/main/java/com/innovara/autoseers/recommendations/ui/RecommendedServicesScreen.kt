package com.innovara.autoseers.recommendations.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.R
import com.innovara.autoseers.api.recommendations.CarInfo
import com.innovara.autoseers.home.ui.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedServicesScreen(
    authState: AuthState.UserAuthenticated,
    getRecommendations: suspend (String) -> Unit,
    state: RecommendationsState,
    carInfoEntered: suspend (String, CarInfo) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        val tokenId = authState.authAuthenticatedModel.getToken()
        getRecommendations(tokenId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recommended services",
                    )
                },
            )
        }
    ) {
        when (state) {
            is RecommendationsState.Loading -> LoadingIndicator()
            is RecommendationsState.Loaded -> {
                val listState = remember { state.recommendations.toMutableStateList() }
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .padding(12.dp)
                ) {
                    item {
                        Text(
                            text = "These are the services that are most likely due based on your current mileage.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                    items(listState, key = { recommendation ->
                        recommendation.serviceName
                    }) { recommendation ->
                        ServiceCard(
                            serviceName = recommendation.serviceName,
                            servicePrice = recommendation.averagePrice,
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }

            else -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val scope = rememberCoroutineScope()
                val animation by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.services_animations))
                var showCarInfoBottomSheet by remember {
                    mutableStateOf(false)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier.size(200.dp),
                        composition = animation,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(
                        text = "We couldn't find a model, make and year for your car",
                        textAlign = TextAlign.Center,
                    )
                    FilledTonalButton(onClick = {
                        showCarInfoBottomSheet = true
                    }) {
                        Text(text = "Enter data manually")
                    }

                    if (showCarInfoBottomSheet) {
                        CarInfoBottomSheet(
                            onDismiss = { showCarInfoBottomSheet = false },
                            data = carListData,
                        ) {
                            scope.launch {
                                val tokenId = authState.authAuthenticatedModel.getToken()
                                carInfoEntered(tokenId, it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarInfoBottomSheet(
    onDismiss: () -> Unit,
    data: CarListData,
    onSubmit: (CarInfo) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = bottomSheetState) {
        val listOfModels = remember {
            mutableStateListOf("")
        }
        var currentStep by remember {
            mutableIntStateOf(0)
        }
        var selectedMake by remember {
            mutableStateOf("")
        }
        var selectedModel by remember {
            mutableStateOf("")
        }
        var selectedYear by remember {
            mutableStateOf("")
        }
        var currentMileage by remember {
            mutableStateOf("")
        }
        LineProgression(steps = 5, currentStep = currentStep)
        Surface(
            modifier = Modifier.systemBarsPadding()
        ) {
            AnimatedContent(targetState = currentStep, label = "animation") { targetState ->
                LazyColumn {
                    when (targetState) {
                        0 -> items(data.carList.keys.toList()) { carListKey ->
                            Text(
                                text = carListKey,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable {
                                        data.carList[carListKey]?.forEach {
                                            listOfModels.add(it)
                                        }
                                        currentStep = 1
                                        selectedMake = carListKey
                                    })
                        }

                        1 -> items(listOfModels) {
                            Text(text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                    .clickable {
                                        selectedModel = it
                                        currentStep = 2
                                    })
                        }

                        2 -> items(years) {
                            Text(text = "$it",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                    .clickable {
                                        currentStep = 3
                                        selectedYear = it.toString()
                                    })
                        }

                        3 -> {
                            item {
                                TextField(
                                    supportingText = {
                                        Text(text = "Current mileage of your car")
                                    },
                                    value = currentMileage,
                                    onValueChange = {
                                        currentMileage = it
                                    },
                                )
                            }
                            item {
                                Button(onClick = { currentStep = 4 }) {
                                    Text(text = "Next")
                                }
                            }
                        }

                        4 -> {
                            item {
                                Text(
                                    text = "Confirm selections",
                                    style = MaterialTheme.typography.displaySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )

                                Text(
                                    text = "Car make",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                                Text(
                                    text = selectedMake,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                )

                                Text(
                                    text = "Car model",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = selectedModel,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )

                                Text(
                                    text = "Car year",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = selectedYear,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = "Car mileage",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = currentMileage,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Button(
                                        modifier = Modifier.align(Alignment.Center),
                                        onClick = {
                                            scope.launch {
                                                bottomSheetState.hide()
                                                onSubmit(
                                                    CarInfo(
                                                        make = selectedMake,
                                                        model = selectedModel,
                                                        year = selectedYear.toInt(),
                                                        mileage = currentMileage
                                                    )
                                                )
                                            }.invokeOnCompletion {
                                                onDismiss()
                                            }
                                        }) {
                                        Text(text = "Confirm")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineProgression(steps: Int, currentStep: Int = 0) {
    val width = LocalConfiguration.current.screenWidthDp
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(steps) {
            val color =
                if (it == currentStep) MaterialTheme.colorScheme.primary else Color.Gray
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(width.dp / steps)
                    .background(color = color)
            )
        }
    }
}

@Composable
fun ServiceCard(
    serviceName: String,
    servicePrice: String,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = serviceName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = "$servicePrice Average price",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Image(
                modifier = Modifier.size(44.dp),
                painter = painterResource(id = R.drawable.small_stars),
                contentDescription = ""
            )
        }
    }
}