package com.innovara.autoseers.api.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

sealed class UploadServiceState {
    data class Failed(
        val reason: String
    ) : UploadServiceState()

    data object Loading : UploadServiceState()
    data object Success : UploadServiceState()
}

sealed class HomeServiceState {
    data object Empty : HomeServiceState()
    data object Loading : HomeServiceState()
    data class Loaded(
        val mileage: Int,
        val alerts: Int,
        val repairs: Int,
        val uploads: Int,
        val health: Int,
        val carModelMake: String = "Unknown",
    ) : HomeServiceState()
}

interface HomeService {
    suspend fun sendReportUpload(tokenId: String, image: ByteArray): Flow<UploadServiceState>
    suspend fun getHomeData(tokenId: String): Flow<HomeServiceState>
}

class HomeServiceImpl @Inject constructor(
    retrofit: Retrofit,
) : HomeService {
    private val homeApi = retrofit.create(HomeApi::class.java)
    override suspend fun sendReportUpload(
        tokenId: String,
        image: ByteArray
    ): Flow<UploadServiceState> = flow {
        emit(UploadServiceState.Loading)
        val request = HomeUploadRequest(tokenId = tokenId, image = image)
        val response = homeApi.uploadReport(request).await()
        when {
            response.failure != "" -> emit(UploadServiceState.Failed(
                "Could not process your image due to ${response.failure} error"
            ))
            else -> emit(UploadServiceState.Success)
        }
    }.catch {
        emit(UploadServiceState.Failed(reason = it.localizedMessage ?: ""))
    }

    override suspend fun getHomeData(tokenId: String): Flow<HomeServiceState> = flow {
        emit(HomeServiceState.Loading)
        val request = HomeRequest(tokenId = tokenId)
        val response = homeApi.fetchHomeData(homeRequest = request).await()
        when {
            response.data != null -> emit(response.data.toHomeServiceLoadedState())
            else -> emit(HomeServiceState.Empty)
        }
    }.catch {
        println(it.message)
        emit(HomeServiceState.Empty)
    }

    private fun HomeData.toHomeServiceLoadedState() = HomeServiceState.Loaded(
        mileage = mileage ?: 0,
        alerts = alerts,
        carModelMake = "$make $model",
        health = healthScore,
        repairs = repairs,
        uploads = reports
    )
}
