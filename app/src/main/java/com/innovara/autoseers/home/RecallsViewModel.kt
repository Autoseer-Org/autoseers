package com.innovara.autoseers.home

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.api.home.AlertsService
import com.innovara.autoseers.api.home.AlertsServiceState
import com.innovara.autoseers.api.home.MarkAsCompleteServiceState
import com.innovara.autoseers.api.home.MarkAsRepairServiceState
import com.innovara.autoseers.api.home.Recall
import com.innovara.autoseers.api.home.RecallsService
import com.innovara.autoseers.api.home.RecallsServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class RecallsState {
    data object Loading : RecallsState()
    data object Empty : RecallsState()
    data object Error : RecallsState()
    data class Loaded(
        val recalls: List<Recall>
    ) : RecallsState()
}

data class MarkAsCompleteModel(
    val nhtsaCampaignNumber: String
)

sealed class MarkAsCompleteState {
    data object Idle : MarkAsCompleteState()
    data object Complete : MarkAsCompleteState()
    data object Loading : MarkAsCompleteState()
    data object Failed : MarkAsCompleteState()
}

@HiltViewModel
class RecallsViewModel @Inject constructor(
    private val recallsService: RecallsService,
) : ViewModel() {
    private val _state: MutableStateFlow<RecallsState> = MutableStateFlow(RecallsState.Empty)
    val state: StateFlow<RecallsState> = _state.asStateFlow()

    private val _markAsCompleteState: MutableStateFlow<MarkAsCompleteState> =
        MutableStateFlow(MarkAsCompleteState.Idle)
    val markAsCompleteState: StateFlow<MarkAsCompleteState> = _markAsCompleteState.asStateFlow()

    suspend fun getRecalls(token: String) = recallsService
        .getRecalls(token)
        .collectLatest { recallsServiceState ->
            when (recallsServiceState) {
                RecallsServiceState.Failed -> _state.update { RecallsState.Error }
                is RecallsServiceState.Loaded -> _state.update {
                    if (recallsServiceState.recalls.isNotEmpty() && recallsServiceState.count > 0)
                        RecallsState.Empty
                    else RecallsState.Loaded(
                        recallsServiceState.recalls
                    )
                }
                RecallsServiceState.Loading -> _state.update { RecallsState.Loading }
            }
        }

    suspend fun markAsComplete(token: String, markAsCompleteModel: MarkAsCompleteModel) {
        recallsService.completeRecall(
            token = token,
            nhtsaCampaignNumber = markAsCompleteModel.nhtsaCampaignNumber
        ).collectLatest {
            when (it) {
                is MarkAsCompleteServiceState.Loading -> _markAsCompleteState.update { MarkAsCompleteState.Loading }
                is MarkAsCompleteServiceState.Failed -> _markAsCompleteState.update { MarkAsCompleteState.Failed }
                is MarkAsCompleteServiceState.Complete -> _markAsCompleteState.update { MarkAsCompleteState.Complete }
            }
        }
    }
}