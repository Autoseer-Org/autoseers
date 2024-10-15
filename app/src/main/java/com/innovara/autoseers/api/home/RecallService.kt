package com.innovara.autoseers.api.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class RecallsServiceState {
    data class Loaded(
        val count: Int,
        val recalls: List<Recall>
    ): RecallsServiceState()
    data object Failed: RecallsServiceState()
    data object Loading: RecallsServiceState()
}

sealed class MarkAsCompleteServiceState {
    data object Failed: MarkAsCompleteServiceState()
    data object Complete: MarkAsCompleteServiceState()
    data object Loading: MarkAsCompleteServiceState()
}

interface RecallsService {
    suspend fun getRecalls(token: String): Flow<RecallsServiceState>
    suspend fun completeRecall(token: String, nhtsaCampaignNumber: String): Flow<MarkAsCompleteServiceState>
}

class RecallsServiceImpl @Inject constructor(
    retrofit: Retrofit
) : RecallsService {
    private val recallsApi = retrofit.create(RecallApi::class.java)
    override suspend fun getRecalls(token: String): Flow<RecallsServiceState> = flow {
        emit(RecallsServiceState.Loading)
        val response = recallsApi.fetchRecalls(token).await()
        when {
            response.failure.isNullOrBlank().not() -> {
                emit(RecallsServiceState.Failed)
            }
            else -> emit(RecallsServiceState.Loaded(count = response.count, recalls = response.recalls))
        }
    }.catch {
        println(it.localizedMessage)
        emit(RecallsServiceState.Failed)
    }

    override suspend fun completeRecall(
        token: String,
        nhtsaCampaignNumber: String
    ): Flow<MarkAsCompleteServiceState> = flow {
        emit(MarkAsCompleteServiceState.Loading)
        val response = recallsApi.completeRecall(
            authorizationToken = token,
            completeRecallRequest = CompleteRecallRequest(nhtsaCampaignNumber = nhtsaCampaignNumber)
        ).await()
        when {
            response.failure.isNullOrBlank().not() -> emit(MarkAsCompleteServiceState.Failed)
            else -> emit(MarkAsCompleteServiceState.Complete)
        }
    }.catch {
        println(it.localizedMessage)
        emit(MarkAsCompleteServiceState.Failed)
    }

}