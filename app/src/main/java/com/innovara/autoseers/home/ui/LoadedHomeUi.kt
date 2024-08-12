package com.innovara.autoseers.home.ui

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.innovara.autoseers.AuthAuthenticatedModel
import com.innovara.autoseers.AuthState
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
    authState: AuthState.UserAuthenticated,
    uploadState: UploadState,
) {
    val scrollableState = rememberScrollState()
    var shouldShowBottomSheet by remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (shouldShowBottomSheet) {
        ModalBottomSheet(onDismissRequest = {
            shouldShowBottomSheet = false
        }) {
            Surface {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = "Health Score of Your Car",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Your car's overall health score is ${homeModel.healthScore}%. " +
                                "This score is a comprehensive measure of your vehicle's condition, " +
                                "derived from a detailed analysis of various key components. " +
                                "It provides an easy-to-understand summary of the health of your car."
                    )
                }
            }
        }
    }
    if (uploadState is UploadState.Uploading) {
        EmptyHomeUi(modifier, uploadState)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollableState)
                .padding(18.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = R.drawable.modern_car), contentDescription = ""
                )
            }
            Text(
                buildAnnotatedString {
                append("Welcome ${authState.authAuthenticatedModel.userName}")
                append(" ")
                appendInlineContent("icon")
            }, inlineContent = mapOf(
                "icon" to InlineTextContent(
                    Placeholder(36.sp, 36.sp, PlaceholderVerticalAlign.TextCenter),
                ) {
                    Text(text = "\uD83D\uDE0A", fontSize = 24.sp)
                }
            ), fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold, fontSize = 26.sp)
            Text(text = "Check on your car’s health and find ways to improve it")
            HealthScoreRow(healthScore = homeModel.healthScore, onInfoIconClicked = {
                scope.launch {
                    bottomSheetState.show()
                }.invokeOnCompletion {
                    if (!shouldShowBottomSheet) {
                        shouldShowBottomSheet = true
                    }
                }
            })
            DashboardSection(
                mileage = homeModel.totalMileage.toString(),
                alerts = homeModel.alerts,
                repairs = homeModel.repairs,
                uploads = homeModel.uploadedReports
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (homeModel.alerts > 0) {
                Text(
                    text = "Things to keep in mind",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                HomeCard(
                    modifier = Modifier.clickable {
                        navigateToAlerts()
                    },
                    title = "You have ${homeModel.alerts} active alerts for your car",
                    description = "Based on your recent reports, we've identified ${homeModel.alerts} issues that need your attention soon."
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (homeModel.repairs > 0) {
                HomeCard(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = .5f),
                    imageRes = R.drawable.check,
                    title = "Congratulations! \n" +
                            "You’ve completed ${homeModel.repairs} repairs",
                    description = "You’ve successfully completed ${homeModel.repairs} repair. Keep going!"
                )
            }
        }
    }
}

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error.copy(alpha = .5f),
    @DrawableRes imageRes: Int = R.drawable.nut,
    title: String = "",
    description: String = "",
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color)
            .padding(vertical = 12.dp)
            .padding(12.dp)
    ) {
        Box {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = imageRes),
                contentDescription = ""
            )
        }
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(description)
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
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Total \nMileage", color = Color.White)
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
            Text(text = "Alerts", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$alerts", color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.build),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = "Repairs", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$repairs", color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.reports),
                contentDescription = "",
                tint = Color.White
            )
            Text(text = "Reports", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "$uploads", color = Color.White)
        }
    }
}

@Composable
private fun HealthScoreRow(
    modifier: Modifier = Modifier,
    onInfoIconClicked: () -> Unit = {},
    healthScore: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onInfoIconClicked() },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$healthScore% ",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
        )
        Text(
            text = "Health Score",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Icon(imageVector = Icons.Filled.Info, contentDescription = "")
    }
}

@Preview
@Composable
fun PreviewLoadedHomeUi() {
    Scaffold {
        CompositionLocalProvider {
            LoadedHomeUi(
                homeModel = HomeModel(40, 12000, 2, 1, 1, "Toyota Corolla"),
                modifier = Modifier.padding(it),
                uploadState = UploadState.Idle,
                authState = AuthState.UserAuthenticated(shouldSkipNameStep = false, authAuthenticatedModel = AuthAuthenticatedModel())
            )
        }
    }
}