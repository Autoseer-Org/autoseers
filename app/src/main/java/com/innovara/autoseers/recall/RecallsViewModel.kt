//package com.innovara.autoseers.recall
//
//import androidx.lifecycle.ViewModel
//import com.innovara.autoseers.api.home.RecallItem
//import com.innovara.autoseers.api.home.RecallsService
//import com.innovara.autoseers.home.HomeState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.serialization.SerialName
//import javax.inject.Inject
//
//sealed class RecallsState {
//    data object Empty : RecallsState()
//    data object Loading : RecallsState()
//    data class Loaded(
//        val recallsModel: RecallsModel
//    ) : RecallsState()
//}
//
//data class RecallsModel(
//    val count: Int,
//    val recalls: List<RecallItem>
//)
//
//data class RecallItem(
//    val shortSummary: String,
//    val nhtsaCampaignNumber: String,
//    val manufacturer:  String,
//    val reportReceivedDate: String,
//    val component: String,
//    val summary: String,
//    val consequence: String,
//    val remedy: String,
//    val notes: String,
//    val status: String
//)
//
//@HiltViewModel
//class RecallsViewModel @Inject constructor(
//    private val recallsService: RecallsService
//) : ViewModel() {
//    private val _recallsState: MutableStateFlow<RecallsState> = MutableStateFlow(RecallsState.Loading)
//    val recallsState: StateFlow<RecallsState> = _recallsState.asStateFlow()
//
//    suspend fun getRecalls(token: String) {
//        recallsService
//            .getRecalls(token)
//            .collectLatest {
//
//            }
//    }
//}