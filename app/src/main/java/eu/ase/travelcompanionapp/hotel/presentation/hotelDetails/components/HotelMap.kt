package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.HotelLocationViewModel

@Composable
fun HotelMap(
    modifier: Modifier = Modifier,
    hotelState: HotelLocationViewModel.HotelState,
    onNavigateToOffers: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()

    val coordinates = hotelState.hotel?.let { Pair(it.latitude, it.longitude) }
    LaunchedEffect(coordinates) {
        coordinates?.let {
            cameraPositionState.position =
                LatLng(it.first, it.second).let { it2 ->
                    CameraPosition.fromLatLngZoom(
                        it2,
                        15f
                    )
                }
        }
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (hotelState.photos.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_photos_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        } else {
            ImageList(
                hotelState = hotelState,
                onImageClick = { index ->
                    currentImageIndex = index
                    isDialogOpen = true
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToOffers,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(R.string.view_offers), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        GoogleMapComponent(
            cameraPositionState = cameraPositionState,
            hotelState = hotelState,
            modifier = Modifier.weight(1f)
        )
    }

    if (isDialogOpen && hotelState.photos.isNotEmpty()) {
        ImageDialog(
            hotelState = hotelState,
            currentImageIndex = currentImageIndex,
            onDismiss = { isDialogOpen = false },
            onImageChange = { index -> currentImageIndex = index }
        )
    }
}