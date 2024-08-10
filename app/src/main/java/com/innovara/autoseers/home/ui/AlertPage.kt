package com.innovara.autoseers.home.ui

import android.icu.util.Calendar
import android.text.style.StyleSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.innovara.autoseers.navigation.routes.homeroute.AlertRoute
import com.innovara.autoseers.R
import com.innovara.autoseers.home.CreateServiceBookingModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertPage(
    onBookAppointment: suspend (CreateServiceBookingModel) -> Unit = {},
    navigateBack: () -> Unit = {},
    markAsRepaired: suspend () -> Unit = {},
    alertArgument: AlertRoute,
    authToken: String,
) {
    Scaffold(
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
        var showBookingBottomSheet by remember {
            mutableStateOf(false)
        }
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
                    tokenId = authToken
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.purple_scribble),
                contentDescription = "",
                modifier = Modifier.size(84.dp)
            )
            Text(
                text = alertArgument.alertName,
                style = MaterialTheme.typography.displayLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = alertArgument.alertState, style = MaterialTheme.typography.displaySmall)
            Text(text = alertArgument.alertCategory, style = MaterialTheme.typography.displaySmall)
            Text(
                text = alertArgument.alertDescription,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showBookingBottomSheet = true
            }) {
                Text(text = "Book appointment")
            }
            TextButton(onClick = {
                scope.launch {
                    markAsRepaired()
                }
            }) {
                Text(text = "Mark as repaired")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    onDismiss: () -> Unit = {},
    partName: String = "",
    tokenId: String = "",
    bookAppointment: suspend (CreateServiceBookingModel) -> Unit = {},
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
        if (showTimePicker) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(onClick = { confirmSelection() }) {
                Text(text = "Confirm selection")
            }
        } else {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "We'll send a request to book your appointment. Check back again to see if your appointment has been confirmed",
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
                    TextField(value = email, onValueChange = {
                        email = it
                    }, supportingText = {
                        Text(text = "Enter your email")
                    })
                    Button(onClick = {
                        showTimePicker = true
                    }) {
                        Text(text = "Schedule a time/date")
                    }
                    val shouldEnableBookingButton =
                        email.isNotBlank() && place.isNotBlank() && showTimePicker.not()
                    Button(
                        enabled = shouldEnableBookingButton,
                        onClick = {
                            scope.launch {
                                bookAppointment(
                                    CreateServiceBookingModel(
                                        place = place,
                                        email = email,
                                        token = tokenId,
                                        partName = partName,
                                        time = timePickerState.toString()
                                    )
                                )
                            }
                        }) {
                        Text(text = "Send booking request")
                    }
                }
            }
        }
    }
}