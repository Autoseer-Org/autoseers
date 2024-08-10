package com.innovara.autoseers.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovara.autoseers.api.home.HomeService
import com.innovara.autoseers.api.home.HomeServiceState
import com.innovara.autoseers.api.home.UploadServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UploadState {
    data object Uploading : UploadState()
    data object Idle : UploadState()
    data class Failed(val reason: String) : UploadState()
    data object Success : UploadState()
}

sealed class HomeState {
    data object Empty : HomeState()
    data object Loading : HomeState()
    data class Loaded(
        val homeModel: HomeModel
    ) : HomeState()
}

data class HomeModel(
    val healthScore: Int,
    val totalMileage: Int,
    val alerts: Int,
    val repairs: Int,
    val uploadedReports: Int,
    val carModelMake: String,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeService: HomeService
) : ViewModel() {
    private val _homeState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    private val _uploadState: MutableStateFlow<UploadState> = MutableStateFlow(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    suspend fun getHomeData(tokenId: String) {
        homeService
            .getHomeData(tokenId)
            .collectLatest(::processHomeServiceState)
    }

    suspend fun uploadImageReport(tokenId: String, image: ByteArray) {
        homeService
            .sendReportUpload(tokenId = tokenId, image = image)
            .collectLatest(::processHomeUploadServiceState)
    }

    private fun processHomeServiceState(homeServiceState: HomeServiceState) {
        when (homeServiceState) {
            is HomeServiceState.Empty -> _homeState.update { HomeState.Empty }
            is HomeServiceState.Loaded -> _homeState.update {
                HomeState.Loaded(homeModel = homeServiceState.toHomeModel())
            }

            is HomeServiceState.Loading -> _homeState.update { HomeState.Loading }
        }
    }

    private fun processHomeUploadServiceState(uploadServiceState: UploadServiceState) {
        when (uploadServiceState) {
            is UploadServiceState.Failed -> {
                _uploadState.update {
                    UploadState.Failed(
                        reason = uploadServiceState.reason ?: ""
                    )
                }
                viewModelScope.launch {
                    delay(3000)
                    _uploadState.update { UploadState.Idle }
                }
            }

            is UploadServiceState.Success -> _uploadState.update { UploadState.Success }
            is UploadServiceState.Loading -> _uploadState.update { UploadState.Uploading }
        }
    }

    private fun HomeServiceState.Loaded.toHomeModel() = HomeModel(
        repairs = repairs,
        alerts = alerts,
        healthScore = health,
        totalMileage = mileage,
        uploadedReports = uploads,
        carModelMake = carModelMake
    )
}