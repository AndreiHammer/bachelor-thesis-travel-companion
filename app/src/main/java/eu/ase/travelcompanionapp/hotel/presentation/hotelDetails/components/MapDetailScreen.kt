package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel
import eu.ase.travelcompanionapp.ui.AppBarStyle
import eu.ase.travelcompanionapp.ui.CompanionTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapDetailScreen(
    viewModel: HotelLocationViewModel,
    onBackClick: () -> Unit
) {
    val hotelState by viewModel.hotelState.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()

    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }

    LaunchedEffect(coordinates) {
        coordinates?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.first, it.second), 15f
            )
        }
    }

    Scaffold(
        topBar = {
            CompanionTopAppBar(
                title = hotelState.hotel?.name!!,
                subtitle = stringResource(R.string.location),
                onNavigationClick = {
                    onBackClick()
                },
                style = AppBarStyle.PINNED
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (coordinates != null) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false,
                        compassEnabled = true,
                        mapToolbarEnabled = true
                    )
                ) {
                    Marker(
                        state = MarkerState(position = LatLng(coordinates.first, coordinates.second)),
                        title = hotelState.hotel?.name ?: "",
                        snippet = hotelState.hotel?.let { hotel ->
                            stringResource(R.string.hotel_in, hotel.countryCode)
                        } ?: ""
                    )
                }
            }
        }
    }
}