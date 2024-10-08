package com.innovara.autoseers.home.ui

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.innovara.autoseers.navigation.routes.homeroute.AlertRoute
import com.innovara.autoseers.R
import com.innovara.autoseers.home.BookingState
import com.innovara.autoseers.home.CreateServiceBookingModel
import com.innovara.autoseers.home.MarkAsRepairModel
import com.innovara.autoseers.home.MarkAsRepairedState
import com.innovara.autoseers.home.PollingBookingStatusState
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertPage(
    onBookAppointment: suspend (String, CreateServiceBookingModel) -> Unit = { _: String, _: CreateServiceBookingModel -> },
    navigateBack: () -> Unit = {},
    markAsRepaired: suspend (String, MarkAsRepairModel) -> Unit = { _: String, _: MarkAsRepairModel -> },
    alertArgument: AlertRoute,
    authToken: String,
    bookingState: BookingState,
    markAsRepairedState: MarkAsRepairedState,
    pollingBookingStatusState: PollingBookingStatusState,
    startPollingForBookingStatus: suspend (token: String, partId: String) -> Unit = { _, _ -> },
    shouldShowBookingsButton: Boolean,
) {
    val snackBarState = remember {
        SnackbarHostState()
    }
    var showBookingBottomSheet by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        startPollingForBookingStatus(authToken, alertArgument.alertId)
    }
    LaunchedEffect(key1 = markAsRepairedState) {
        scope.launch {
            if (markAsRepairedState is MarkAsRepairedState.Repaired) {
                snackBarState.showSnackbar(
                    message = "Congratulations! You've repaired one part",
                    duration = SnackbarDuration.Short
                )
            }
        }.invokeOnCompletion {
            if (markAsRepairedState is MarkAsRepairedState.Repaired) {
                showBookingBottomSheet = false
                navigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Part") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        }
    ) {
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .padding(it)
                .padding(12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showBookingBottomSheet) {
                BookingBottomSheet(
                    onDismiss = {
                        showBookingBottomSheet = false
                    },
                    partName = alertArgument.alertName,
                    bookAppointment = onBookAppointment,
                    tokenId = authToken,
                    partId = alertArgument.alertId,
                    bookingState = bookingState
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.purple_scribble),
                contentDescription = "",
                modifier = Modifier.size(84.dp)
            )
            Text(
                text = alertArgument.alertName,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(text = alertArgument.alertState, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = alertArgument.alertCategory,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = alertArgument.alertDescription,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (shouldShowBookingsButton) {
                BookingSection(pollingBookingStatusState = pollingBookingStatusState) { startBookingProcess ->
                    showBookingBottomSheet = startBookingProcess
                }
            }
            TextButton(onClick = {
                scope.launch {
                    markAsRepaired(
                        authToken,
                        MarkAsRepairModel(
                            partId = alertArgument.alertId
                        )
                    )
                }
            }) {
                Text(text = "Mark as repaired")
            }
        }
    }
}

@Composable
fun BookingSection(
    pollingBookingStatusState: PollingBookingStatusState,
    onBookingClicked: (Boolean) -> Unit
) {
    when(pollingBookingStatusState) {
        is PollingBookingStatusState.NotBooked -> Button(onClick = {
            onBookingClicked(true)
        }) {
            Text(text = "Book appointment")
        }
        is PollingBookingStatusState.WaitingToBeBooked, PollingBookingStatusState.Booked, PollingBookingStatusState.Processing -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val composition by rememberLottieComposition(
                    spec = if (pollingBookingStatusState is PollingBookingStatusState.Booked) {
                        LottieCompositionSpec.RawRes(
                            R.raw.animation
                        )
                    } else {
                        LottieCompositionSpec.RawRes(
                            R.raw.animation1
                        )
                    }
                )
                LottieAnimation(
                    modifier = Modifier.size(100.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                when (pollingBookingStatusState) {
                    is PollingBookingStatusState.WaitingToBeBooked -> Text(text = "Processing booking request")
                    is PollingBookingStatusState.Processing -> Text(text = "Booking appointment")
                    is PollingBookingStatusState.Booked -> Text(text = "Appointment booked")
                    else -> Unit
                }
            }
        }
        is PollingBookingStatusState.Idle -> CircularProgressIndicator(modifier = Modifier.size(14.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    onDismiss: () -> Unit = {},
    partName: String = "",
    tokenId: String = "",
    partId: String = "",
    bookingState: BookingState,
    bookAppointment: suspend (String, CreateServiceBookingModel) -> Unit = { token: String, createServiceBookingModel: CreateServiceBookingModel -> },
) {
    val recommendedPlaces = remember {
        mutableStateListOf("")
    }
    var place by remember {
        mutableStateOf("")
    }
    val currentContext = LocalContext.current
    Places.initializeWithNewPlacesApiEnabled(
        currentContext,
        "AIzaSyBSiErxHGoilzIVlbeyHcTdF8ur0H9Nhds"
    )
    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
    )
    val datePickerState = rememberDatePickerState()
    val selectedDate = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            convertMillisToDate(it)
        }
    }
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var showTimePicker by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val confirmSelection = {
        showTimePicker = false
    }

    var email by remember {
        mutableStateOf("")
    }

    var showEmailField by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }) {
        var isSearchActive by remember {
            mutableStateOf(false)
        }
        var hasPickedAPlace by remember {
            mutableStateOf(false)
        }
        var hasConfirmedScheduledTime by remember {
            mutableStateOf(false)
        }
        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = {
                showDatePicker = false
            }, confirmButton = {
                Button(onClick = {
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text(text = "Confirm selected date")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }
        if (showTimePicker) {
            Dialog(onDismissRequest = { showTimePicker = false }) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(
                            state = timePickerState,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Button(onClick = { confirmSelection() }) {
                            Text(text = "Confirm selection")
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dateFormat = SimpleDateFormat("EEE, d MMM yy HH:mm", Locale.getDefault())
                val dateString by remember {
                    selectedDate?.year?.let { calendar.set(Calendar.YEAR, it) }
                    selectedDate?.month?.let { calendar.set(Calendar.MONTH, it) }
                    selectedDate?.day?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }

                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    mutableStateOf(dateFormat.format(calendar.time))
                }
                Text(
                    text = "We'll send a request to book your appointment. Check back again later to see if your appointment has been confirmed",
                    style = MaterialTheme.typography.bodySmall
                )
                if (hasPickedAPlace.not()) {
                    SearchBar(active = isSearchActive, query = place, onQueryChange = {
                        place = it
                    }, placeholder = {
                        Text(text = "Enter your car repair shop")
                    }, onActiveChange = {
                        isSearchActive = it
                    }, onSearch = {
                        recommendedPlaces.clear()
                        val placesClient = Places.createClient(currentContext)
                        val placeFields = listOf(Place.Field.ID, Place.Field.NAME)
                        val circleBound =
                            RectangularBounds.newInstance(
                                LatLngBounds(
                                    LatLng(25.665621, -80.351082),
                                    LatLng(
                                        25.761681,
                                        -80.191788
                                    )
                                )
                            )
                        val searchByTextRequest = SearchByTextRequest.builder(it, placeFields)
                            .setLocationRestriction(circleBound)
                            .build()
                        placesClient.searchByText(searchByTextRequest)
                            .addOnSuccessListener {
                                it.places.forEach { location ->
                                    recommendedPlaces.add(
                                        location.name ?: ""
                                    )
                                }
                            }
                            .addOnFailureListener {
                                println(it.localizedMessage)
                            }
                    }) {
                        LazyColumn {
                            items(recommendedPlaces) {
                                Text(text = it, modifier = Modifier
                                    .padding(12.dp)
                                    .clickable {
                                        isSearchActive = false
                                        place = it
                                        hasPickedAPlace = true
                                    })
                            }
                        }
                    }
                } else {
                    if (showEmailField) {
                        TextField(value = email, onValueChange = {
                            email = it
                        }, supportingText = {
                            Text(text = "Enter your email")
                        })
                    }
                    if (hasConfirmedScheduledTime.not()) {
                        TextButton(onClick = {
                            showDatePicker = true
                        }) {
                            Text(text = "Schedule a time/date")
                        }
                        Text(
                            text = "Selected time: $dateString",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = {
                            hasConfirmedScheduledTime = true
                            showEmailField = true
                        }) {
                            Text(text = "Confirm time/date picked")
                        }
                    }
                    val shouldEnableBookingButton =
                        email.isNotBlank() && place.isNotBlank() && showTimePicker.not()
                    if (hasConfirmedScheduledTime) {
                        Button(
                            enabled = shouldEnableBookingButton,
                            onClick = {
                                scope.launch {
                                    bookAppointment(
                                        tokenId,
                                        CreateServiceBookingModel(
                                            place = place,
                                            email = email,
                                            partName = partName,
                                            time = dateString,
                                            id = partId
                                        )
                                    )
                                }
                            }) {
                            when (bookingState) {
                                is BookingState.Loading -> CircularProgressIndicator(color = Color.White)
                                is BookingState.Success -> {
                                    Text("Booking sent")
                                    LaunchedEffect(key1 = bookingState) {
                                        scope.launch {
                                            sheetState.hide()
                                        }.invokeOnCompletion {
                                            onDismiss()
                                        }
                                    }
                                }

                                is BookingState.Idle -> Text(text = "Send booking request")
                                else -> Text(text = "Failed to send request")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): Date = Date(millis)