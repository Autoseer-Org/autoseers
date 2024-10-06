package com.innovara.autoseers.recommendations.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovara.autoseers.api.recommendations.CarInfo
import com.innovara.autoseers.api.recommendations.Recommendation
import com.innovara.autoseers.api.recommendations.RecommendationServiceServiceState
import com.innovara.autoseers.api.recommendations.RecommendationsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecommendationsState {
    data object Idle : RecommendationsState()
    data class Loaded(
        val recommendations: List<Recommendation>,
        val manualEntryState: ManualEntryState = ManualEntryState.NO_ACTION,
    ) : RecommendationsState()

    data object Loading : RecommendationsState()
    data object Failure : RecommendationsState()
}

enum class ManualEntryState {
    SUCCESSFUL,
    FAILED,
    NO_ACTION,
}

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsService: RecommendationsService,
) : ViewModel() {
    private val _state: MutableStateFlow<RecommendationsState> =
        MutableStateFlow(RecommendationsState.Idle)
    val state: StateFlow<RecommendationsState> = _state.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, error ->
        Log.e("Error in Home ViewModel: ", "${error.message}. In context: $context")
        context.cancel()
    }

    suspend fun getRecommendations(token: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            recommendationsService.getRecommendations(tokenId = token)
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

    suspend fun submitManualCarInfo(token: String, carInfo: CarInfo) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            recommendationsService.sendCarInfo(carInfo = carInfo, token = token)
                .collectLatest { wasSuccessful ->
                    if (wasSuccessful) {
                        getRecommendations(token = token)
                    }
                }
        }
}