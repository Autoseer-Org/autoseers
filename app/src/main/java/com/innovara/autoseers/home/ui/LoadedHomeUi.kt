package com.innovara.autoseers.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.innovara.autoseers.R
import com.innovara.autoseers.home.HomeModel
import com.innovara.autoseers.home.UploadState
import com.innovara.autoseers.utils.toFormattedNumber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadedHomeUi(
    homeModel: HomeModel,
    modifier: Modifier = Modifier,
    navigateToAlerts: () -> Unit = {},
    navigateToRecalls: () -> Unit = {},
    uploadState: UploadState,
) {
    val scrollableState = rememberScrollState()
    var shouldShowBottomSheet by remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedSheet: CarInfoSectionType? by remember {
        mutableStateOf(null)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (shouldShowBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                shouldShowBottomSheet = false
            }) {
            Surface {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .padding(vertical = 24.dp)
                ) {
                    when (selectedSheet) {
                        CarInfoSectionType.HEALTH -> {
                            Text(
                                text = "Your Vehicle's Condition Score",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Your vehicle’s condition score is ${homeModel.healthScore}%, powered " +
                                        "by Gemini AI using your car's checkpoint report. It gives you a" +
                                        " quick rundown of your car's health, but just a heads up—it’s " +
                                        "AI-driven, so it might not always be 100% accurate.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        CarInfoSectionType.PRICE -> {
                            Text(
                                text = "Your Vehicle's estimated value",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "This estimate gives you a quick snapshot of your car's current market value, based on several key factors:\n" +
                                        "\n" +
                                        "\n• Make & Model: How your car's brand and type stack up in the market.\n" +
                                        "\n• Year: The age of your vehicle plays a role in its value.\n" +
                                        "\n• Mileage: Higher mileage can affect resale value, while lower mileage keeps it higher.\n" +
                                        "\n• Condition: The overall health and maintenance of your car, reflected in its condition score, impacts the estimate.\n" +
                                        "\n\nKeep in mind, this estimate is AI-generated and may vary from actual market prices. Always consider consulting a professional for a more detailed appraisal.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        null -> Unit
                    }
                }
            }
        }
    }
    if (uploadState is UploadState.Uploading) {
        EmptyHomeUi(modifier, uploadState)
    } else {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        Column(
            modifier = modifier
                .drawBehind {
                    val yOffset = scrollableState.value * 1.2
                    this.center.copy(0f, 0f)
                    drawRect(
                        topLeft = Offset(0f, -yOffset.toFloat()),
                        size = Size(
                            height = screenHeight.dp.toPx() * .5f,
                            width = screenWidth.dp.toPx()
                        ),
                        brush = Brush.radialGradient(
                            listOf(Color.Transparent, Color.Black),
                            radius = .1.dp.toPx(),
                        ),
                        style = Fill
                    )
                }
                .graphicsLayer {
                    clip = true
                }
                .fillMaxSize()
                .verticalScroll(scrollableState)
                .padding(18.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(38.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                Text(
                    text = homeModel.carModelMake,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Stay in the loop with your car’s health! Get custom alerts, a value estimate," +
                        " and recommended services just for you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            // Contains information about the health of the car and the estimated car price
            CarInfoRow(
                healthScore = homeModel.healthScore,
                estimatedCarPrice = homeModel.estimatedCarPrice,
                onInfoIconClicked = { carInfoType ->
                    scope.launch {
                        bottomSheetState.show()
                    }.invokeOnCompletion {
                        if (!shouldShowBottomSheet) {
                            shouldShowBottomSheet = true
                            selectedSheet = carInfoType
                        }
                    }
                })
            DashboardSection(
                mileage = homeModel.totalMileage.toString(),
                alerts = homeModel.alerts,
                repairs = homeModel.repairs,
                uploads = homeModel.uploadedReports
            )
            if (homeModel.alerts > 0) {
                val horizontalScrollState = rememberScrollState()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Things to Keep in Mind!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Important alerts and updates to stay on top of your car's health.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(horizontalScrollState, enabled = true)
                ) {
                    HomeCard(
                        title = "Your car alerts",
                        description = "Based on your recent reports, we've identified ${homeModel.alerts} issues that need your attention soon."
                    ) { navigateToAlerts() }
                    if (homeModel.repairs > 0) {
                        HomeCard(
                            imageRes = R.drawable.check,
                            title = "Congratulations!",
                            description = "You’ve successfully completed ${homeModel.repairs} repair. Keep going!"
                        ) { Unit }
                    }
                }
            }
            if (homeModel.recalls != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Check your recalls",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                HomeCard(
                    title = "You have ${homeModel.recalls} recalls for your car",
                    description = "Based on the data we pulled, we've identified ${homeModel.recalls} open recalls for your car's make, model and year."
                ) {
                    navigateToRecalls()
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    @DrawableRes imageRes: Int = R.drawable.nut,
    title: String = "",
    description: String = "",
    onCardPress: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.clickable { onCardPress() }) {
        val style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        var titleStyle by remember {
            mutableStateOf(style)
        }
        Column(
            modifier = Modifier
                .wrapContentSize()
                .requiredWidthIn(max = 200.dp)
                .height(250.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = imageRes),
                    contentDescription = "",
                )
            }
            Text(
                text = title,
                style = titleStyle,
                softWrap = false,
                modifier = Modifier.widthIn(max = 150.dp),
                onTextLayout = { textLayout ->
                    if (textLayout.didOverflowWidth) {
                        titleStyle = titleStyle.copy(fontSize = titleStyle.fontSize * .9)
                    }
                }
            )
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DashboardSection(
    modifier: Modifier = Modifier,
    mileage: String,
    alerts: Int,
    repairs: Int,
    uploads: Int,
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Total \nMileage",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = mileage.toFormattedNumber(),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.fire_alerts),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = "Alerts", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$alerts", color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.build),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = "Repairs", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$repairs", color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.reports),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = "Reports", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$uploads", color = Color.White)
        }
    }
}

enum class CarInfoSectionType {
    HEALTH,
    PRICE,
}

@Composable
private fun CarInfoRow(
    modifier: Modifier = Modifier,
    onInfoIconClicked: (CarInfoSectionType) -> Unit = {},
    healthScore: Int,
    estimatedCarPrice: String,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        CarInfoSection(
            sectionTitle = "Vehicle condition",
            carInfoSectionType = CarInfoSectionType.HEALTH,
            modifier = modifier,
            onInfoIconClicked = onInfoIconClicked,
            displayValue = healthScore
        )
        if (estimatedCarPrice.isNotBlank()) {
            CarInfoSection(
                sectionTitle = "Price estimate",
                carInfoSectionType = CarInfoSectionType.PRICE,
                modifier = modifier,
                onInfoIconClicked = onInfoIconClicked,
                displayValue = estimatedCarPrice
            )
        }
    }
}


@Composable
fun <T> CarInfoSection(
    sectionTitle: String,
    carInfoSectionType: CarInfoSectionType,
    modifier: Modifier,
    onInfoIconClicked: (CarInfoSectionType) -> Unit,
    displayValue: T
) {
    Column(
        modifier = modifier
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(
                onClick = { onInfoIconClicked(carInfoSectionType) }, modifier = Modifier.padding(
                    PaddingValues(0.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(18.dp),
                )
            }
        }
        when (carInfoSectionType) {
            CarInfoSectionType.HEALTH -> Text(
                text = "${displayValue as Int}% ",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onPrimary
            )

            CarInfoSectionType.PRICE -> {
                Text(
                    text = "$displayValue",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(4.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewLoadedHomeUi() {
    Scaffold {
        CompositionLocalProvider {
            LoadedHomeUi(
                homeModel = HomeModel(40, 12000, 2, 1, 1, "Toyota Corolla", 2, "$12000.00"),
                modifier = Modifier.padding(it),
                uploadState = UploadState.Idle,
            )
        }
    }
}