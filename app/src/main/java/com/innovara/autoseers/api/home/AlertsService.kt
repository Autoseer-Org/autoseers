package com.innovara.autoseers.api.home

import com.innovara.autoseers.di.firebase.FirebaseService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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

sealed class MarkAsRepairServiceState {
    data object Loading : MarkAsRepairServiceState()
    data object Repaired : MarkAsRepairServiceState()
    data object Failed : MarkAsRepairServiceState()
}

sealed class PollBookingStatusServiceState {
    data object Failure : PollBookingStatusServiceState()
    data class Loaded(
        val state: BookingState,
    ) : PollBookingStatusServiceState()
}

interface AlertsService {
    suspend fun getAlerts(token: String): Flow<AlertsServiceState>
    suspend fun bookAppointment(token: String, appointmentBookingRequest: AppointmentBookingRequest): Flow<BookAppointmentState>
    suspend fun markAsRepaired(token: String, markAsRepairRequest: MarkAsRepairedRequest): Flow<MarkAsRepairServiceState>
    suspend fun pollBookingStatus(token: String, pollBookingStatusRequest: PollBookingStatusRequest): Flow<PollBookingStatusServiceState>
    fun renderBookingButton(): Flow<Boolean>
}

class AlertsServiceImpl @Inject constructor(
    retrofit: Retrofit,
    firebaseService: FirebaseService,
) : AlertsService {
    private val api = retrofit.create(AlertsApi::class.java)
    private val remoteConfig = firebaseService.remoteConfig()

    override suspend fun getAlerts(token: String): Flow<AlertsServiceState> = flow {
        emit(AlertsServiceState.Loading)
        val response = api.fetchAlerts(authHeader = token).await()
        when {
            response.data == null -> emit(AlertsServiceState.Failed)
            response.data.isEmpty() -> emit(AlertsServiceState.Loaded(emptyList()))
            else -> emit(AlertsServiceState.Loaded(response.data.sortedBy { it.category }))
        }
    }.catch {
        println(it.localizedMessage)
        emit(AlertsServiceState.Failed)
    }

    override suspend fun bookAppointment(token: String, appointmentBookingRequest: AppointmentBookingRequest): Flow<BookAppointmentState> =
        flow {
            emit(BookAppointmentState.Loading)
            val response = api.bookAppointment(authHeader = token, appointmentBookingRequest = appointmentBookingRequest).await()
            when {
                response.failure == null -> emit(BookAppointmentState.AppointmentProcessed)
                else -> emit(BookAppointmentState.Failed)
            }
        }.catch {
            emit(BookAppointmentState.Failed)
        }

    override suspend fun markAsRepaired(token: String, markAsRepairRequest: MarkAsRepairedRequest): Flow<MarkAsRepairServiceState> =
        flow {
            emit(MarkAsRepairServiceState.Loading)
            val response = api.markAsRepaired(authHeader = token, markAsRepairRequest).await()
            when {
                response.failure == null -> emit(MarkAsRepairServiceState.Repaired)
                else -> emit(MarkAsRepairServiceState.Failed)
            }
        }.catch {
            emit(MarkAsRepairServiceState.Failed)
        }

    override suspend fun pollBookingStatus(token: String, pollBookingStatusRequest: PollBookingStatusRequest): Flow<PollBookingStatusServiceState> =
        flow {
            while (true) {
                // Polling every 8s
                val response = api.pollBookingStatus(authHeader = token, pollBookingStatusRequest = pollBookingStatusRequest).await()
                when {
                    response.failure != null -> emit(PollBookingStatusServiceState.Failure)
                    else -> emit(PollBookingStatusServiceState.Loaded(state = response.state))
                }
                delay(4000L)
            }
        }

    override fun renderBookingButton(): Flow<Boolean> = flow {
        emit(remoteConfig.getBoolean("bookings"))
    }

}