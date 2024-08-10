package com.innovara.autoseers.home.ui

import androidx.compose.foundation.background
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
import com.innovara.autoseers.home.model.AlertModel
import com.innovara.autoseers.utils.toDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsPage(
    state: AlertsState,
    onBackPress: () -> Unit,
) {
    when (state) {
        is AlertsState.Loading -> {
            LoadingIndicator()
        }
        is AlertsState.Loaded -> {
            val listState = remember {
                state.alerts.toMutableStateList()
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Alerts") },
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
                            text = "You have ${listState.size} alerts for your car",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            text = "Based on the reports you’ve uploaded, we’ve found 5 different problems you need to take care of soon",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    items(listState, key = { alert ->
                        alert.name
                    }) { alert ->
                        AlertCard(
                            modifier = Modifier.clip(shape = RoundedCornerShape(12.dp)),
                            alertModel = AlertModel(
                                alert.name,
                                alert.category,
                                alert.updatedDate,
                                alert.status,
                                alert.summary ?: ""
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
fun AlertCard(
    modifier: Modifier = Modifier,
    alertModel: AlertModel,
) {
    val modifierColor = when (alertModel.status) {
        "Medium" -> modifier.background(color = MaterialTheme.colorScheme.onError.copy(alpha = .5f))
        "Bad" -> modifier.background(color = MaterialTheme.colorScheme.error.copy(alpha = .5f))
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
                    text = alertModel.category,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = true,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = alertModel.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = true,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(modifier = Modifier, horizontalAlignment = Alignment.End) {
                Box {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = alertModel.status, style = MaterialTheme.typography.bodyMedium)
                        Text(text = alertModel.updatedDate.toDate(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Row {
            Text(
                text = alertModel.summary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@Preview
fun PreviewAlertCard(
    alertModel: AlertModel = AlertModel(
        "Something",
        "Interior",
        "10/30/202",
        "Medium",
        ""
    )
) {
    AlertCard(alertModel = alertModel)
}