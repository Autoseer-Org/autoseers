package com.innovara.autoseers.api.recommendations

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

interface RecommendationsService {
    suspend fun getRecommendations(recommendationsRequest: RecommendationsRequest): Flow<RecommendationServiceServiceState>
}

class RecommendationsServiceImpl @Inject constructor(
    retrofit: Retrofit,
) : RecommendationsService {
    private val api = retrofit.create(RecommendationsApi::class.java)
    override suspend fun getRecommendations(recommendationsRequest: RecommendationsRequest): Flow<RecommendationServiceServiceState> =
        flow {
            emit(RecommendationServiceServiceState.Loading)
            val response = api.fetchRecommendations(recommendationsRequest).await()
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
}