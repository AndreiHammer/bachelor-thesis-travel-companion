package eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar

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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CompanionTopAppBar(
                title = stringResource(R.string.map_title),
                onNavigationClick = { onAction(LocationSearchAction.OnBackClick) },
                scrollBehavior = scrollBehavior,
                actions = {
                    FilterButton(
                        isExpanded = isFilterExpanded,
                        onClick = { isFilterExpanded = !isFilterExpanded }
                    )
                },
                style = eu.ase.travelcompanionapp.ui.AppBarStyle.PINNED
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            if (locationPermissionState.status is PermissionStatus.Granted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings().copy(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = true
                    ),
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

                AnimatedVisibility(
                    visible = isFilterExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .zIndex(1f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 450.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
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

                                isFilterExpanded = false
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

                if (isFilterExpanded && markerPosition == null) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.select_location_on_map),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterButton(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = stringResource(R.string.filters))
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp 
                       else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
