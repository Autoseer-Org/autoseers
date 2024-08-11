package com.innovara.autoseers.api.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class ManualEntryServiceState {
    data class Failed(
        val reason: String
    ): ManualEntryServiceState()
    data object Successful: ManualEntryServiceState()
    data object Loading: ManualEntryServiceState()
}

interface ManualEntryService {
    suspend fun manuallyEnterCardData(tokenId: String, year: String, make: String, model: String, mileage: String): Flow<ManualEntryServiceState>
}

class ManualEntryServiceImpl @Inject constructor(
    retrofit: Retrofit
): ManualEntryService {
    private val manualEntryApi = retrofit.create(ManualEntryApi::class.java)
    override suspend fun manuallyEnterCardData(
        tokenId: String,
        year: String,
        make: String,
        model: String,
        mileage: String,
        ): Flow<ManualEntryServiceState> = flow {
        emit(ManualEntryServiceState.Loading)
        val response = manualEntryApi.manuallyEnterCarData(ManualEntryRequest(
            token = tokenId,
            year = year,
            make = make,
            model= model,
            mileage = mileage,
        )).await()
        when {
            response.failure.isNullOrBlank().not() -> emit(ManualEntryServiceState.Failed(
                reason = "Could not enter your car info due to ${response.failure}"
            ))
            else -> emit(ManualEntryServiceState.Successful)
        }
    }.catch {
        emit(ManualEntryServiceState.Failed(reason = it.localizedMessage))
    }

}