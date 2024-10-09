package com.innovara.autoseers.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.home.AlertsState
import com.innovara.autoseers.home.RecallsState
import com.innovara.autoseers.home.model.AlertModel
import com.innovara.autoseers.home.model.RecallModel
import com.innovara.autoseers.navigation.routes.homeroute.AlertRoute
import com.innovara.autoseers.navigation.routes.homeroute.AlertsRoute
import com.innovara.autoseers.navigation.routes.homeroute.RecallRoute
import com.innovara.autoseers.utils.toDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallsPage(
    state: RecallsState,
    onBackPress: () -> Unit = {},
    navigateToRecall: (RecallRoute) -> Unit = {},
) {
    when (state) {
        is RecallsState.Loading -> {
            LoadingIndicator()
        }

        is RecallsState.Loaded -> {
            val listState = remember {
                state.recalls.toMutableStateList()
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Recalls") },
                        navigationIcon = {
                            IconButton(onClick = { onBackPress() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = ""
                                )
                            }
                        },
                    )
                }
            ) {
                val lazyListState = rememberLazyListState()
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = "You have ${listState.size} recalls for your car",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(listState, key = { recall ->
                        recall.nhtsaCampaignNumber
                    }) { recall ->
                        RecallCard(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    navigateToRecall(
                                        RecallRoute(

                                        )
                                    )
                                },
                            recallModel = RecallModel(
                                recall.nhtsaCampaignNumber,
                                recall.manufacturer,
                                recall.reportReceivedDate,
                                recall.component,
                                recall.summary,
                                recall.consequence,
                                recall.remedy,
                                recall.notes,
                                recall.status
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        else -> Surface {

        }
    }
}

@Composable
fun RecallCard(
    modifier: Modifier = Modifier,
    recallModel: RecallModel,
) {
    val modifierColor = when (recallModel.status) {
        "Incomplete" -> modifier.background(color = MaterialTheme.colorScheme.error.copy(alpha = .5f))
        else -> modifier
    }
    Column(modifierColor.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.width(134.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = recallModel.component,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = true,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(modifier = Modifier, horizontalAlignment = Alignment.End) {
                Box {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = recallModel.status, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = recallModel.reportReceivedDate,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewRecallCard(
    recallModel: RecallModel = RecallModel(
        "some",
        "Toyota",
        "10/30/2023",
        "Steering:Column",
        "Summary",
        "Consequence",
        "",
        "",
        "Incomplete"
    )
) {
    RecallCard(recallModel = recallModel)
}