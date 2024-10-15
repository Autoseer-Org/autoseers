package com.innovara.autoseers.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.innovara.autoseers.R
import com.innovara.autoseers.home.BookingState
import com.innovara.autoseers.home.MarkAsCompleteModel
import com.innovara.autoseers.home.MarkAsCompleteState
import com.innovara.autoseers.home.MarkAsRepairModel
import com.innovara.autoseers.home.MarkAsRepairedState
import com.innovara.autoseers.home.PollingBookingStatusState
import com.innovara.autoseers.navigation.routes.homeroute.AlertRoute
import com.innovara.autoseers.navigation.routes.homeroute.RecallRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallPage(
    navigateBack: () -> Unit = {},
    markAsComplete: suspend (String, MarkAsCompleteModel) -> Unit = { token: String, markAsCompleteModel: MarkAsCompleteModel -> },
    recallArgument: RecallRoute,
    authToken: String,
    markAsCompleteState: MarkAsCompleteState,
) {
    val snackBarState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Part") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        }
    ) {
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .padding(it)
                .padding(12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.purple_scribble),
                contentDescription = "",
                modifier = Modifier.size(84.dp)
            )
            Text(
                text = recallArgument.component,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
//            Row {
//                Text(text = recallArgument.status, style = MaterialTheme.typography.bodyMedium)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(text = recallArgument., style = MaterialTheme.typography.bodyMedium)
//            }
            Text(
                text = recallArgument.summary,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = recallArgument.summary,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = recallArgument.consequence,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = recallArgument.remedy,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = {
                scope.launch {
                    markAsComplete(
                        authToken,
                        MarkAsCompleteModel(
                            nhtsaCampaignNumber = recallArgument.nhtsaCampaignNumber
                        )
                    )
                }
            }) {
                Text(text = "Mark as complete")
            }
        }
    }
}