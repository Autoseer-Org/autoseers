package com.innovara.autoseers.recommendations.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.R
import com.innovara.autoseers.home.ui.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedServicesScreen(
    authState: AuthState.UserAuthenticated,
    getRecommendations: suspend (String) -> Unit,
    state: RecommendationsState
) {
    LaunchedEffect(key1 = Unit) {
        getRecommendations(authState.authAuthenticatedModel.tokenId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Services",
                        style = MaterialTheme.typography.displayMedium
                    )
                },
            )
        }
    ) {
        when (val uiState = state) {
            is RecommendationsState.Loading -> LoadingIndicator()
            is RecommendationsState.Loaded -> {
                val listState = remember { uiState.recommendations.toMutableStateList() }
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
                    items(listState, key = {
                        it.serviceName
                    }) {
                        ServiceCard(
                            serviceName = it.serviceName,
                            servicePrice = it.averagePrice,
                            description = it.description
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            is RecommendationsState.Failure -> {
                Text(text = "Something happen. Try again later")
            }

            else -> Unit
        }
    }
}


@Composable
fun ServiceCard(
    serviceName: String,
    servicePrice: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0XffD9D9D9))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = serviceName, style = MaterialTheme.typography.displaySmall)
                Text(text = "$servicePrice Average price", style = MaterialTheme.typography.bodyMedium)
            }
            Image(
                modifier = Modifier.size(44.dp),
                painter = painterResource(id = R.drawable.small_stars),
                contentDescription = ""
            )
        }
        Text(text = description, style = MaterialTheme.typography.bodySmall)
    }
}