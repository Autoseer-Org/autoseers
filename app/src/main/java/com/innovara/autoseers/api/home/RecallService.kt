package com.innovara.autoseers.api.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class RecallsServiceState {
    data class Failed(
        val reason: String
    ): RecallsServiceState()
    data class Loaded(
        val count: Int?,
        val recalls: List<RecallItem>?
    ): RecallsServiceState()
    data object Loading: RecallsServiceState()
    data object Empty: RecallsServiceState()
    data object NavigateToManualEntry: RecallsServiceState()
}

sealed class CompleteRecallServiceState {
    data class Failed(
        val reason: String
    ): CompleteRecallServiceState()
    data object Successful: CompleteRecallServiceState()
    data object Loading: CompleteRecallServiceState()
}

interface RecallsService {
    suspend fun getRecalls(tokenId: String): Flow<RecallsServiceState>
    suspend fun completeRecall(tokenId: String, nhtsaCampaignNumber: String): Flow<CompleteRecallServiceState>
}

class RecallsServiceImpl @Inject constructor(
    retrofit: Retrofit
) : RecallsService {
    private val recallsApi = retrofit.create(RecallApi::class.java)
    override suspend fun getRecalls(tokenId: String): Flow<RecallsServiceState> = flow {
        emit(RecallsServiceState.Loading)
        val response = recallsApi.fetchRecalls(tokenId).await()
        when {
            response.failure.isNullOrBlank().not() -> {
                if (response.failure.equals("Incomplete car info")) {
                    emit(RecallsServiceState.NavigateToManualEntry)
                } else {
                    emit(RecallsServiceState.Failed(
                        "Could not fetch recalls due to ${response.failure}"
                    ))
                }
            }
            response.count == 0 -> emit(RecallsServiceState.Empty)
            else -> emit(RecallsServiceState.Loaded(count = response.count, recalls = response.recalls))
        }
    }.catch {
        println(it.localizedMessage)
        emit(RecallsServiceState.Failed(reason = it.localizedMessage))
    }

    override suspend fun completeRecall(
        tokenId: String,
        nhtsaCampaignNumber: String
    ): Flow<CompleteRecallServiceState> = flow {
        emit(CompleteRecallServiceState.Loading)
        val response = recallsApi.completeRecall(CompleteRecallRequest(
            token = tokenId,
            nhtsaCampaignNumber = nhtsaCampaignNumber)
        ).await()
        when {
            response.failure.isNullOrBlank().not() -> emit(CompleteRecallServiceState.Failed(
                reason = "Could not complete your recall due to ${response.failure}"
            ))
            else -> emit(CompleteRecallServiceState.Successful)
        }
    }.catch {
        println(it.localizedMessage)
        emit(CompleteRecallServiceState.Failed(reason = it.localizedMessage))
    }

}