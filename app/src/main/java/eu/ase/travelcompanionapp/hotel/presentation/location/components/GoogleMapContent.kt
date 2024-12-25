package eu.ase.travelcompanionapp.hotel.presentation.location.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.location.HotelLocationViewModel

@Composable
fun GoogleMapComponent(
    cameraPositionState: CameraPositionState,
    hotelState: HotelLocationViewModel.HotelState,
    modifier: Modifier = Modifier
) {
    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }

    GoogleMap(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState
    ) {
        if (coordinates != null) {
            Marker(
                state = MarkerState(position = LatLng(coordinates.first, coordinates.second)),
                title = hotelState.hotel.name,
                snippet = stringResource(R.string.hotel_in, hotelState.hotel.countryCode)
            )
        }
    }
}

