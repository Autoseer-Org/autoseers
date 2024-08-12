package com.innovara.autoseers.home

import androidx.lifecycle.ViewModel
import com.innovara.autoseers.api.home.Alert
import com.innovara.autoseers.api.home.AlertsService
import com.innovara.autoseers.api.home.AlertsServiceState
import com.innovara.autoseers.api.home.AppointmentBookingRequest
import com.innovara.autoseers.api.home.BookAppointmentState
import com.innovara.autoseers.api.home.MarkAsRepairServiceState
import com.innovara.autoseers.api.home.MarkAsRepairedRequest
import com.innovara.autoseers.api.home.PollBookingStatusRequest
import com.innovara.autoseers.api.home.PollBookingStatusServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.innovara.autoseers.api.home.BookingState as DataBookingState

sealed class AlertsState {
    data object Loading : AlertsState()
    data object Empty : AlertsState()
    data object Error : AlertsState()
    data class Loaded(
        val alerts: List<Alert>
    ) : AlertsState()
}

data class CreateServiceBookingModel(
    val token: String,
    val partName: String,
    val email: String,
    val place: String,
    val time: String,
    val id: String,
)

sealed class BookingState {
    data object Idle : BookingState()
    data object Loading : BookingState()
    data object Failed : BookingState()
    data object Success : BookingState()
}

data class MarkAsRepairModel(
    val token: String,
    val partId: String
)

sealed class MarkAsRepairedState {
    data object Idle : MarkAsRepairedState()
    data object Repaired : MarkAsRepairedState()
    data object Loading : MarkAsRepairedState()
    data object Failed : MarkAsRepairedState()
}

sealed class PollingBookingStatusState {
    data object Idle : PollingBookingStatusState()
    data object Booked : PollingBookingStatusState()
    data object NotBooked : PollingBookingStatusState()
    data object WaitingToBeBooked : PollingBookingStatusState()
    data object Processing : PollingBookingStatusState()
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertsService: AlertsService,
) : ViewModel() {
    private val _state: MutableStateFlow<AlertsState> = MutableStateFlow(AlertsState.Empty)
    val state: StateFlow<AlertsState> = _state.asStateFlow()

    private val _bookingState: MutableStateFlow<BookingState> = MutableStateFlow(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private val _repairedState: MutableStateFlow<MarkAsRepairedState> =
        MutableStateFlow(MarkAsRepairedState.Idle)
    val repairedState: StateFlow<MarkAsRepairedState> = _repairedState.asStateFlow()

    private val _pollingBookingStatusState: MutableStateFlow<PollingBookingStatusState> =
        MutableStateFlow(PollingBookingStatusState.Idle)
    val pollingBookingStatusState = _pollingBookingStatusState.asStateFlow()

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

    suspend fun bookAppointment(createServiceBookingModel: CreateServiceBookingModel) {
        alertsService.bookAppointment(
            createServiceBookingModel.toAppointmentBookingRequest()
        ).collectLatest {
            when (it) {
                is BookAppointmentState.Loading -> _bookingState.update {
                    BookingState.Loading
                }

                is BookAppointmentState.Failed -> _bookingState.update {
                    BookingState.Failed
                }

                is BookAppointmentState.AppointmentProcessed -> _bookingState.update {
                    BookingState.Success
                }
            }
        }
    }

    suspend fun markAsRepair(markAsRepairModel: MarkAsRepairModel) {
        alertsService.markAsRepaired(markAsRepairModel.toMarkAsRepairRequest()).collectLatest {
            when (it) {
                is MarkAsRepairServiceState.Loading -> _repairedState.update { MarkAsRepairedState.Loading }
                is MarkAsRepairServiceState.Failed -> _repairedState.update { MarkAsRepairedState.Failed }
                is MarkAsRepairServiceState.Repaired -> _repairedState.update { MarkAsRepairedState.Repaired }
            }
        }
    }

    suspend fun pollBookingStatus(token: String, partId: String) {
        alertsService.pollBookingStatus(PollBookingStatusRequest(token = token, partId = partId))
            .collect {
                when (it) {
                    is PollBookingStatusServiceState.Loaded -> {
                        val pollState = it.state
                        if (pollState == DataBookingState.NO_BOOKING_REQUESTED) {
                            _pollingBookingStatusState.update { PollingBookingStatusState.NotBooked }
                        }
                        if (pollState == DataBookingState.BOOKED) {
                            _pollingBookingStatusState.update { PollingBookingStatusState.Booked }
                        }
                        if (pollState == DataBookingState.WAITING_TO_BE_BOOKED) {
                            _pollingBookingStatusState.update { PollingBookingStatusState.WaitingToBeBooked }
                        }

                        if (pollState == DataBookingState.PROCESSING) {
                            _pollingBookingStatusState.update { PollingBookingStatusState.Processing }
                        }
                    }

                    else -> _pollingBookingStatusState.update { PollingBookingStatusState.NotBooked }
                }
            }
    }

    private fun MarkAsRepairModel.toMarkAsRepairRequest() = MarkAsRepairedRequest(
        partId = partId,
        token = token,
    )

    private fun CreateServiceBookingModel.toAppointmentBookingRequest() = AppointmentBookingRequest(
        token = this.token,
        email = this.email,
        place = this.place,
        timeDate = this.time,
        id = this.id
    )
}