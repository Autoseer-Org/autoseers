package com.innovara.autoseers.home

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.api.home.Alert
import com.innovara.autoseers.api.home.AlertsService
import com.innovara.autoseers.api.home.AlertsServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class AlertsState {
    data object Loading : AlertsState()
    data object Empty : AlertsState()
    data object Error : AlertsState()
    data class Loaded(
        val alerts: List<Alert>
    ) : AlertsState()
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertsService: AlertsService,
): ViewModel() {
    private val _state: MutableStateFlow<AlertsState> = MutableStateFlow(AlertsState.Empty)
    val state: StateFlow<AlertsState> = _state.asStateFlow()
    suspend fun getAlerts(token: String) = alertsService
        .getAlerts(token)
        .collectLatest { alertsServiceState ->
            when (alertsServiceState) {
                AlertsServiceState.Failed -> _state.update { AlertsState.Error }
                is AlertsServiceState.Loaded -> _state.update {
                    if (alertsServiceState.list.isEmpty()) AlertsState.Empty else AlertsState.Loaded(
                        alertsServiceState.list
                    )
                }

                AlertsServiceState.Loading -> _state.update { AlertsState.Loading }
            }
        }
}