package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel

@Composable
fun GoogleMapComponent(
    cameraPositionState: CameraPositionState,
    hotelState: HotelLocationViewModel.HotelState,
    modifier: Modifier = Modifier
) {
    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }

    if (coordinates == null || (coordinates.first == 0.0 && coordinates.second == 0.0)) {
        Box(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.no_location_data),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        GoogleMap(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp)),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = LatLng(coordinates.first, coordinates.second)),
                title = hotelState.hotel.name,
                snippet = stringResource(R.string.hotel_in, hotelState.hotel.countryCode)
            )
        }
    }
}

