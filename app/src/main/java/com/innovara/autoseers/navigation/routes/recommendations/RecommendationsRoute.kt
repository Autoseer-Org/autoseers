package com.innovara.autoseers.navigation.routes.recommendations

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.innovara.autoseers.AuthState
import com.innovara.autoseers.navigation.routes.GlobalRoute
import com.innovara.autoseers.recommendations.ui.RecommendationsViewModel
import com.innovara.autoseers.recommendations.ui.RecommendedServicesScreen
import kotlinx.serialization.Serializable

@Serializable
object RecommendationsRoute : GlobalRoute() {
    override fun toString(): String = "RecommendationsRoute"
}

fun NavGraphBuilder.buildRecommendedServices(
    authState: AuthState
) {
    composable<RecommendationsRoute> {
        if (authState is AuthState.UserAuthenticated) {
            val recommendationsViewModel: RecommendationsViewModel = hiltViewModel()
            val state by recommendationsViewModel.state.collectAsState()
            RecommendedServicesScreen(
                authState = authState,
                getRecommendations = recommendationsViewModel::getRecommendations,
                state = state
            )
        }
    }
}