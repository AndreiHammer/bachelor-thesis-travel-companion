package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel

@Composable
fun HotelMap(
    modifier: Modifier = Modifier,
    hotelState: HotelLocationViewModel.HotelState
) {
    val cameraPositionState = rememberCameraPositionState()

    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }
    LaunchedEffect(coordinates) {
        coordinates?.let {
            cameraPositionState.position =
                it.first.let { it1 -> it.second.let { it2 -> LatLng(it1, it2) } }.let { it2 ->
                    CameraPosition.fromLatLngZoom(
                        it2,
                        14f
                    )
                }
        }
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ImageList(
            hotelState = hotelState,
            onImageClick = { index ->
                currentImageIndex = index
                isDialogOpen = true
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(8.dp))


        GoogleMapComponent(
            cameraPositionState = cameraPositionState,
            hotelState = hotelState,
            modifier = Modifier.weight(1f)
        )
    }

    if (isDialogOpen) {
        ImageDialog(
            hotelState = hotelState,
            currentImageIndex = currentImageIndex,
            onDismiss = { isDialogOpen = false },
            onImageChange = { index -> currentImageIndex = index }
        )
    }
}