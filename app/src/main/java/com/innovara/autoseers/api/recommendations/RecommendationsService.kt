package com.innovara.autoseers.api.recommendations

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class RecommendationServiceServiceState {
    data object Loading : RecommendationServiceServiceState()
    data class Loaded(
        val data: List<Recommendation>
    ) : RecommendationServiceServiceState()

    data object Failure : RecommendationServiceServiceState()
}

data class CarInfo(
    val make: String,
    val model: String,
    val year: Int,
    val mileage: String,
)

interface RecommendationsService {
    suspend fun getRecommendations(tokenId: String): Flow<RecommendationServiceServiceState>
    suspend fun sendCarInfo(carInfo: CarInfo, token: String): Flow<Boolean>
}

class RecommendationsServiceImpl @Inject constructor(
    retrofit: Retrofit
) : RecommendationsService {
    private val api = retrofit.create(RecommendationsApi::class.java)
    override suspend fun getRecommendations(tokenId: String): Flow<RecommendationServiceServiceState> =
        flow {
            emit(RecommendationServiceServiceState.Loading)
            val response = api.fetchRecommendations(tokenId).await()
            when {
                response.error.isNullOrBlank()
                    .not() -> emit(RecommendationServiceServiceState.Failure)

                response.data != null && response.data.recommendations.isEmpty().not() -> emit(
                    RecommendationServiceServiceState.Loaded(
                        response.data.recommendations
                    )
                )
            }
        }.catch {
            emit(RecommendationServiceServiceState.Failure)
        }

    override suspend fun sendCarInfo(carInfo: CarInfo, token: String): Flow<Boolean> = flow {
        val response = api.manualEntryForCarInfo(
            authHeader = token,
            manualEntryRequest = carInfo.toManualEntryRequest()
        ).await()
        when {
            response.failure.isNullOrBlank() -> emit(true)
            else -> emit(false)
        }
    }.catch {
        Log.e("Error sending car info", it.localizedMessage ?: "")
        emit(false)
    }

    private fun CarInfo.toManualEntryRequest() = ManualEntryRequest(
        make = make,
        model = model,
        year = year.toString(),
        mileage = mileage
    )
}