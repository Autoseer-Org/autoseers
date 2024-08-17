package com.innovara.autoseers.recommendations.ui

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.api.recommendations.Recommendation
import com.innovara.autoseers.api.recommendations.RecommendationServiceServiceState
import com.innovara.autoseers.api.recommendations.RecommendationsRequest
import com.innovara.autoseers.api.recommendations.RecommendationsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class RecommendationsState {
    data object Idle : RecommendationsState()
    data class Loaded(
        val recommendations: List<Recommendation>
    ) : RecommendationsState()

    data object Loading : RecommendationsState()
    data object Failure : RecommendationsState()
}

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsService: RecommendationsService,
) : ViewModel() {
    private val _state: MutableStateFlow<RecommendationsState> =
        MutableStateFlow(RecommendationsState.Idle)
    val state: StateFlow<RecommendationsState> = _state.asStateFlow()

    suspend fun getRecommendations(token: String) {
        recommendationsService.getRecommendations(RecommendationsRequest(token))
            .collectLatest { recommendations ->
                when (recommendations) {
                    is RecommendationServiceServiceState.Loaded -> _state.update {
                        RecommendationsState.Loaded(
                            recommendations.data
                        )
                    }

                    is RecommendationServiceServiceState.Loading -> _state.update { RecommendationsState.Loading }
                    is RecommendationServiceServiceState.Failure -> _state.update { RecommendationsState.Failure }
                }
            }
    }
}