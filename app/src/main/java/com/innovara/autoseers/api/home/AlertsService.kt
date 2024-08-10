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

interface AlertsService {
    suspend fun getAlerts(token: String): Flow<AlertsServiceState>
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
}