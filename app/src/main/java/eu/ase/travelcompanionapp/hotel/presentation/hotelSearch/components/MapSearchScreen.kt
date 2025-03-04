package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.LocationSearchAction
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.filters.LocationFilterSearch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    onAction: (LocationSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var range by remember { mutableIntStateOf(10) }
    var selectedHotelRating by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedHotelAmenities by remember { mutableStateOf<Set<String>>(emptySet()) }
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var adults by remember { mutableIntStateOf(1) }
    var isFilterExpanded by remember { mutableStateOf(false) }

    // Reset previous selections when searching by location
    onAction(LocationSearchAction.OnRatingSelected(emptySet()))
    onAction(LocationSearchAction.OnAmenitiesSelected(emptySet()))
    onAction(LocationSearchAction.OnOfferDetailsSet("", "", 0))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(44.4268, 0.0), 6f)
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        if (locationPermissionState.status !is PermissionStatus.Granted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.map_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(LocationSearchAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFilterExpanded = !isFilterExpanded }) {
                        Icon(
                            imageVector = if (isFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.toggle_filters)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            if (locationPermissionState.status is PermissionStatus.Granted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings().copy(myLocationButtonEnabled = true),
                    properties = remember { MapProperties(isMyLocationEnabled = true) },
                    onMapClick = { latLng -> markerPosition = latLng }
                ) {
                    markerPosition?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = stringResource(R.string.selected_location)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    AnimatedVisibility(
                        visible = isFilterExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            LocationFilterSearch(
                                range = range,
                                onSearchClick = { amenities, ratings, checkIn, checkOut, adultCount ->
                                    markerPosition?.let { location ->
                                        onAction(
                                            LocationSearchAction.OnLocationSelected(
                                                location,
                                                range
                                            )
                                        )
                                    }
                                    selectedHotelRating = ratings
                                    selectedHotelAmenities = amenities
                                    checkInDate = checkIn
                                    checkOutDate = checkOut
                                    adults = adultCount

                                    onAction(
                                        LocationSearchAction.OnOfferDetailsSet(
                                            checkIn,
                                            checkOut,
                                            adultCount
                                        )
                                    )
                                },
                                initialSelectedRatings = selectedHotelRating,
                                initialSelectedAmenities = selectedHotelAmenities,
                                initialCheckInDate = checkInDate,
                                initialCheckOutDate = checkOutDate,
                                initialAdults = adults,
                                onAction = onAction,
                                onRangeChange = { newRange -> range = newRange }
                            )
                        }
                    }
                }

            }
        }
    }
}
