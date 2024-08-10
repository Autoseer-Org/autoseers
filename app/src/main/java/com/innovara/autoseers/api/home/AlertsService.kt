package com.innovara.autoseers.api.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject


sealed class AlertsServiceState {
    data object Loading : AlertsServiceState()
    data class Loaded(
        val list: List<Alert>
    ) : AlertsServiceState()

    data object Failed : AlertsServiceState()
}

sealed class BookAppointmentState {
    data object Loading : BookAppointmentState()
    data object AppointmentProcessed : BookAppointmentState()
    data object Failed : BookAppointmentState()
}

sealed class MarkAsRepairState {
    data object Loading: MarkAsRepairState()
    data object Repaired: MarkAsRepairState()
    data object Failed: MarkAsRepairState()
}

interface AlertsService {
    suspend fun getAlerts(token: String): Flow<AlertsServiceState>
    suspend fun bookAppointment(appointmentBookingRequest: AppointmentBookingRequest): Flow<BookAppointmentState>
    suspend fun markAsRepaired(markAsRepairRequest: MarkAsRepairRequest): Flow<MarkAsRepairState>
}

class AlertsServiceImpl @Inject constructor(
    retrofit: Retrofit
) : AlertsService {
    private val api = retrofit.create(AlertsApi::class.java)
    override suspend fun getAlerts(token: String): Flow<AlertsServiceState> = flow {
        emit(AlertsServiceState.Loading)
        val response = api.fetchAlerts(AlertsRequest(token)).await()
        when {
            response.data == null -> emit(AlertsServiceState.Failed)
            response.data.isEmpty() -> emit(AlertsServiceState.Loaded(emptyList()))
            else -> emit(AlertsServiceState.Loaded(response.data.sortedBy { it.category }))
        }
    }.catch {
        println(it.localizedMessage)
        emit(AlertsServiceState.Failed)
    }

    override suspend fun bookAppointment(appointmentBookingRequest: AppointmentBookingRequest): Flow<BookAppointmentState> =
        flow {
            emit(BookAppointmentState.Loading)
            val response = api.bookAppointment(appointmentBookingRequest).await()
            when {
                response.failure == null -> emit(BookAppointmentState.AppointmentProcessed)
                else -> emit(BookAppointmentState.Failed)
            }
        }.catch {
            emit(BookAppointmentState.Failed)
        }

    override suspend fun markAsRepaired(markAsRepairRequest: MarkAsRepairRequest): Flow<MarkAsRepairState> = flow {
        emit(MarkAsRepairState.Loading)
    }
}