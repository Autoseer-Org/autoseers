package com.innovara.autoseers.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    val shouldShowBookingButton = alertsService.renderBookingButton()
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, error ->
        Log.e("Error in Alert ViewModel: ", "${error.message}. In context: $context")
        context.cancel()
    }

    suspend fun getAlerts(token: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            alertsService
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

    suspend fun bookAppointment(
        token: String,
        createServiceBookingModel: CreateServiceBookingModel
    ) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        alertsService.bookAppointment(
            token = token,
            appointmentBookingRequest = createServiceBookingModel.toAppointmentBookingRequest()
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

    suspend fun markAsRepair(token: String, markAsRepairModel: MarkAsRepairModel) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            alertsService.markAsRepaired(
                token = token,
                markAsRepairRequest = markAsRepairModel.toMarkAsRepairRequest()
            ).collectLatest {
                when (it) {
                    is MarkAsRepairServiceState.Loading -> _repairedState.update { MarkAsRepairedState.Loading }
                    is MarkAsRepairServiceState.Failed -> _repairedState.update { MarkAsRepairedState.Failed }
                    is MarkAsRepairServiceState.Repaired -> _repairedState.update { MarkAsRepairedState.Repaired }
                }
            }
        }

    suspend fun pollBookingStatus(token: String, partId: String) =
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            alertsService.pollBookingStatus(
                token = token,
                pollBookingStatusRequest = PollBookingStatusRequest(partId = partId)
            )
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
    )

    private fun CreateServiceBookingModel.toAppointmentBookingRequest() = AppointmentBookingRequest(
        email = this.email,
        place = this.place,
        timeDate = this.time,
        id = this.id
    )
}